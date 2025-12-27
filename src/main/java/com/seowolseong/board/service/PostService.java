package com.seowolseong.board.service;

import com.seowolseong.board.domain.FileStatus;
import com.seowolseong.board.domain.Post;
import com.seowolseong.board.dto.PostDto;
import com.seowolseong.board.error.ApiException;
import com.seowolseong.board.error.ErrorCode;
import com.seowolseong.board.repository.PostFileRepository;
import com.seowolseong.board.repository.PostRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final FileService fileService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public PostService(PostRepository postRepository,
                       PostFileRepository postFileRepository,
                       FileService fileService) {
        this.postRepository = postRepository;
        this.postFileRepository = postFileRepository;
        this.fileService = fileService;
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public Long create(String title, String content, String postPassword) {

        if (title == null || title.isBlank()) {
            throw new ApiException(ErrorCode.POST_TITLE_REQUIRED);
        }
        if (content == null || content.isBlank()) {
            throw new ApiException(ErrorCode.POST_CONTENT_REQUIRED);
        }
        if (postPassword == null || postPassword.isBlank()) {
            throw new ApiException(ErrorCode.POST_PASSWORD_REQUIRED);
        }

        Post post = new Post();
        post.setTitle(title.trim());
        post.setContent(content);
        post.setPasswordHash(encoder.encode(postPassword));

        Post saved = postRepository.save(post);
        return saved.getId();
    }

    /**
     * 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PostDto> list() {
        return postRepository
                .findTop50ByDeletedAtIsNullOrderByCreatedAtDescIdDesc()
                .stream()
                .map(p -> new PostDto(
                        p.getId(),
                        p.getTitle(),
                        null,
                        p.getCreatedAt(),
                        null,
                        postFileRepository.existsByPostIdAndDeletedAtIsNull(p.getId()),
                        null
                ))
                .toList();
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDto detail(Long id) {
        Post p = findActivePostOrThrow(id);

        var files = postFileRepository
                .findByPostIdAndStatusAndDeletedAtIsNull(id, FileStatus.READY)
                .stream()
                .map(f -> new PostDto.FileItem(
                        f.getId(),
                        f.getOriginalName(),
                        f.getSizeBytes(),
                        f.getContentType()
                ))
                .toList();

        return new PostDto(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                !files.isEmpty(),
                files
        );
    }

    /**
     * 게시글 삭제 (soft delete)
     */
    @Transactional
    public void deletePost(Long id, String postPassword) {
        Post p = findActivePostOrThrow(id);
        verifyPasswordOrThrow(p, postPassword);

        p.setDeletedAt(OffsetDateTime.now());
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updatePost(Long id, String title, String content, String postPassword) {
        Post p = findActivePostOrThrow(id);
        verifyPasswordOrThrow(p, postPassword);

        if (title == null || title.isBlank()) {
            throw new ApiException(ErrorCode.POST_TITLE_REQUIRED);
        }
        if (content == null || content.isBlank()) {
            throw new ApiException(ErrorCode.POST_CONTENT_REQUIRED);
        }

        p.setTitle(title.trim());
        p.setContent(content);
    }

    /**
     * 게시글 비밀번호 검증
     */
    @Transactional(readOnly = true)
    public void verifyPasswordOrThrow(Long id, String postPassword) {
        Post p = findActivePostOrThrow(id);
        verifyPasswordOrThrow(p, postPassword);
    }

    /**
     * 삭제되지 않은 게시글 조회
     */
    private Post findActivePostOrThrow(Long id) {
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        if (p.getDeletedAt() != null) {
            throw new ApiException(ErrorCode.POST_ALREADY_DELETED);
        }
        return p;
    }

    /**
     * 게시글 비밀번호 검증
     */
    private void verifyPasswordOrThrow(Post p, String postPassword) {
        if (postPassword == null || postPassword.isBlank()) {
            throw new ApiException(ErrorCode.POST_PASSWORD_REQUIRED);
        }
        if (!encoder.matches(postPassword, p.getPasswordHash())) {
            throw new ApiException(ErrorCode.POST_PASSWORD_MISMATCH);
        }
    }
}
