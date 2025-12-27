package com.seowolseong.board.api;

import com.seowolseong.board.service.FileService;
import com.seowolseong.board.service.model.FileDownloadResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 파일 업로드 / 다운로드 / 삭제 API
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 파일 다운로드
     * GET /api/files/{fileId}/download
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable Long fileId) {
        FileDownloadResource r = fileService.loadForDownload(fileId);

        String contentType = (r.contentType() == null || r.contentType().isBlank())
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : r.contentType();

        String originalName = (r.originalName() == null || r.originalName().isBlank())
                ? "download"
                : r.originalName();

        String encoded = URLEncoder.encode(originalName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        StreamingResponseBody body = out -> {
            try (InputStream in = r.inputStream()) {
                in.transferTo(out);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + safeAsciiFallback(originalName) +
                                "\"; filename*=UTF-8''" + encoded)
                .contentLength(r.sizeBytes() == null ? -1 : r.sizeBytes())
                .body(body);
    }

    /**
     * 파일 삭제 (게시글 비밀번호 검증)
     * POST /api/files/{fileId}/delete
     */
    @PostMapping("/{fileId}/delete")
    public ResponseEntity<?> delete(@PathVariable Long fileId, @RequestBody FileDeleteRequest req) {
        fileService.deleteFile(fileId, req.getPostPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * 파일 업로드 (여러 파일)
     * POST /api/files/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestParam Long postId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        List<Long> fileIds = fileService.uploadFiles(postId, files);
        return ResponseEntity.ok(Map.of("ok", true, "fileIds", fileIds));
    }

    /**
     * Content-Disposition filename ASCII fallback
     */
    private static String safeAsciiFallback(String name) {
        if (name == null || name.isBlank()) return "download";
        return name.replaceAll("[^\\x20-\\x7E]", "_");
    }

    /**
     * 파일 삭제 요청 DTO
     */
    public static class FileDeleteRequest {
        private String postPassword;

        public String getPostPassword() {
            return postPassword;
        }

        public void setPostPassword(String postPassword) {
            this.postPassword = postPassword;
        }
    }
}
