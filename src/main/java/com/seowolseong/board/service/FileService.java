package com.seowolseong.board.service;

import com.seowolseong.board.domain.FileStatus;
import com.seowolseong.board.domain.Post;
import com.seowolseong.board.domain.PostFile;
import com.seowolseong.board.error.ApiException;
import com.seowolseong.board.error.ErrorCode;
import com.seowolseong.board.repository.PostFileRepository;
import com.seowolseong.board.repository.PostRepository;
import com.seowolseong.board.service.model.FileDownloadResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileService {

    private final PostFileRepository postFileRepository;
    private final S3Client s3Client;
    private final FileRecordService fileRecordService;

    private final PostRepository postRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${app.s3.bucket}")
    private String bucket;

    // 업로드 제한(크기/타입)
    private static final long MAX_FILE_BYTES = 20L * 1024 * 1024;
    private static final long BYTES_UPLOAD_THRESHOLD = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "application/pdf"
    );

    public FileService(
            PostFileRepository postFileRepository,
            S3Client s3Client,
            FileRecordService fileRecordService,
            PostRepository postRepository
    ) {
        this.postFileRepository = postFileRepository;
        this.s3Client = s3Client;
        this.fileRecordService = fileRecordService;
        this.postRepository = postRepository;
    }

    /**
     * 파일 업로드 (PENDING → S3 업로드 → READY/FAILED)
     */
    public List<Long> uploadFiles(Long postId, List<MultipartFile> files) {

        // 입력값 검증
        if (postId == null) {
            throw new ApiException(ErrorCode.REQUIRED_FIELD_MISSING, "postId가 필요합니다.");
        }
        if (files == null || files.isEmpty()) {
            throw new ApiException(ErrorCode.FILE_UPLOAD_EMPTY);
        }

        // 게시글 존재 확인
        if (!postRepository.existsById(postId)) {
            throw new ApiException(ErrorCode.POST_NOT_FOUND);
        }

        List<Long> ids = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            // 파일 메타데이터 구성
            String originalName = sanitizeFilename(file.getOriginalFilename());
            String contentType = (file.getContentType() != null && !file.getContentType().isBlank())
                    ? file.getContentType()
                    : "application/octet-stream";
            long size = file.getSize();

            // 업로드 정책 검증
            if (size > MAX_FILE_BYTES) {
                throw new ApiException(ErrorCode.FILE_UPLOAD_TOO_LARGE);
            }
            if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
                throw new ApiException(ErrorCode.FILE_CONTENT_TYPE_NOT_ALLOWED);
            }

            // S3 키 생성
            String key = "posts/" + postId + "/" + UUID.randomUUID() + "_" + originalName;

            // 메타데이터(PENDING) 생성
            PostFile pf = new PostFile();
            pf.setPostId(postId);
            pf.setOriginalName(originalName);
            pf.setContentType(contentType);
            pf.setSizeBytes(size);
            pf.setStoredKey(key);
            pf.setStatus(FileStatus.PENDING);
            pf.setErrorMessage(null);

            pf = fileRecordService.createPending(pf);
            ids.add(pf.getId());

            // S3 업로드 + 상태 갱신
            try {
                PutObjectRequest putReq = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build();

                if (size <= BYTES_UPLOAD_THRESHOLD) {
                    s3Client.putObject(putReq, RequestBody.fromBytes(file.getBytes()));
                } else {
                    Path tmp = Files.createTempFile("upload-", ".tmp");
                    try {
                        file.transferTo(tmp.toFile());
                        s3Client.putObject(putReq, RequestBody.fromFile(tmp));
                    } finally {
                        Files.deleteIfExists(tmp);
                    }
                }

                fileRecordService.markReady(pf.getId());

            } catch (Exception e) {
                fileRecordService.markFailed(pf.getId(), shortMsg(e));
            }
        }

        return ids;
    }

    /**
     * 파일 다운로드 리소스 조회 (READY만 허용)
     */
    public FileDownloadResource loadForDownload(Long fileId) {

        // 파일 메타 조회
        PostFile pf = postFileRepository.findById(fileId)
                .orElseThrow(() -> new ApiException(ErrorCode.FILE_NOT_FOUND));

        // 삭제/상태 체크
        if (pf.getDeletedAt() != null || pf.getStatus() == FileStatus.DELETED) {
            throw new ApiException(ErrorCode.FILE_ALREADY_DELETED);
        }
        if (pf.getStatus() != FileStatus.READY) {
            throw new ApiException(ErrorCode.FILE_NOT_READY);
        }

        // S3 키 검증
        String key = pf.getStoredKey();
        if (key == null || key.isBlank()) {
            throw new ApiException(ErrorCode.S3_KEY_INVALID, "파일 키가 없습니다.");
        }

        // S3 스트림 획득
        try {
            ResponseInputStream<?> in = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );

            return new FileDownloadResource(
                    (InputStream) in,
                    pf.getOriginalName(),
                    pf.getContentType(),
                    pf.getSizeBytes()
            );

        } catch (NoSuchKeyException e) {
            throw new ApiException(ErrorCode.STORAGE_DOWNLOAD_FAILED, "S3에 파일이 없습니다.");
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new ApiException(ErrorCode.STORAGE_DOWNLOAD_FAILED, "S3에 파일이 없습니다.");
            }
            throw new ApiException(ErrorCode.STORAGE_DOWNLOAD_FAILED, "S3 다운로드 실패");
        } catch (Exception e) {
            throw new ApiException(ErrorCode.FILE_DOWNLOAD_FAILED, "파일 다운로드 처리 실패");
        }
    }

    /**
     * 파일 삭제 (soft delete + S3 삭제)
     */
    @Transactional
    public void deleteFile(Long fileId, String postPassword) {

        // 입력값 검증
        if (fileId == null) {
            throw new ApiException(ErrorCode.REQUIRED_FIELD_MISSING, "fileId가 필요합니다.");
        }
        if (postPassword == null || postPassword.isBlank()) {
            throw new ApiException(ErrorCode.POST_PASSWORD_REQUIRED);
        }

        // 파일 메타 조회
        PostFile pf = postFileRepository.findById(fileId)
                .orElseThrow(() -> new ApiException(ErrorCode.FILE_NOT_FOUND));

        // 이미 삭제된 파일 방어
        if (pf.getDeletedAt() != null || pf.getStatus() == FileStatus.DELETED) {
            throw new ApiException(ErrorCode.FILE_ALREADY_DELETED);
        }

        // 게시글 조회 + 비밀번호 검증
        Post post = postRepository.findById(pf.getPostId())
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        String hash = post.getPasswordHash();
        if (hash == null || hash.isBlank() || !encoder.matches(postPassword, hash)) {
            throw new ApiException(ErrorCode.POST_PASSWORD_MISMATCH);
        }

        // DB soft delete
        pf.delete();

        // S3 객체 삭제
        try {
            String key = pf.getStoredKey();
            if (key != null && !key.isBlank()) {
                DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
                s3Client.deleteObject(delReq);
            }
        } catch (Exception e) {
            throw new ApiException(ErrorCode.STORAGE_DELETE_FAILED, "S3 파일 삭제 실패");
        }
    }

    /**
     * 예외 메시지 축약 (DB 저장용)
     */
    private String shortMsg(Exception e) {
        String m = e.getMessage();
        if (m == null) return e.getClass().getSimpleName();
        return (m.length() > 500) ? m.substring(0, 500) : m;
    }

    /**
     * 파일명 정리
     */
    public static String sanitizeFilename(String original) {
        if (original == null) return "file";

        String name = Paths.get(original).getFileName().toString();
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");

        if (name.length() > 100) {
            int dot = name.lastIndexOf('.');
            if (dot > 0) {
                String ext = name.substring(dot);
                name = name.substring(0, 100 - ext.length()) + ext;
            } else {
                name = name.substring(0, 100);
            }
        }
        return name;
    }
}
