package com.seowolseong.board.api;

import com.seowolseong.board.dto.PostListItemResponse;
import com.seowolseong.board.dto.PostListItemResponse.PostPasswordRequest;
import com.seowolseong.board.dto.PostListItemResponse.PostUpdateRequest;
import com.seowolseong.board.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 게시글 REST API 컨트롤러
 *
 * - HTTP 요청/응답 처리 전담
 * - 비즈니스 로직은 PostService에 위임
 * - DTO를 통해 외부(API)와 내부(Service)를 분리
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 게시글 목록 조회
     * GET /api/posts
     *
     * - 삭제되지 않은 게시글을 최신순으로 조회
     * - 목록에서는 content / files 상세 정보는 내려주지 않음
     */
    @GetMapping
    public List<PostListItemResponse> list() {
        return postService.list();
    }

    /**
     * 게시글 상세 조회
     * GET /api/posts/{id}
     *
     * - 게시글 본문(content)과 첨부파일 목록까지 포함
     * - 삭제된 게시글은 404 반환
     */
    @GetMapping("/{id}")
    public PostListItemResponse detail(@PathVariable Long id) {
        return postService.detail(id);
    }

    /**
     * 게시글 생성
     * POST /api/posts
     *
     * - multipart/form-data 요청
     * - 텍스트(title, content, postPassword) + 첨부파일(files) 처리
     * - 파일은 여러 개 업로드 가능
     */
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("postPassword") String postPassword,
            @RequestPart(value = "files", required = false)
            List<MultipartFile> files
    ) throws Exception {

        Long postId = postService.create(title, content, postPassword, files);

        return ResponseEntity.ok(Map.of("id", postId));
    }

    /**
     * 게시글 비밀번호 검증
     * POST /api/posts/{id}/verify-password
     *
     * - 게시글 수정/삭제 전 비밀번호 확인 용도
     * - 비밀번호 불일치 시 403 반환
     */
    @PostMapping("/{id}/verify-password")
    public ResponseEntity<?> verifyPassword(
            @PathVariable Long id,
            @RequestBody PostPasswordRequest req
    ) {
        postService.verifyPasswordOrThrow(id, req.postPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * 게시글 삭제
     * POST /api/posts/{id}/delete
     *
     * - 비밀번호 검증 후 소프트 삭제 처리
     * - 실제 데이터는 삭제하지 않고 deletedAt만 설정
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestBody PostPasswordRequest req
    ) {
        postService.deletePost(id, req.postPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * 게시글 수정
     * POST /api/posts/{id}/update
     *
     * - 제목/내용 수정
     * - 비밀번호 검증 필수
     */
    @PostMapping("/{id}/update")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest req
    ) {
        postService.updatePost(id, req.title(), req.content(), req.postPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }
    
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(
            Map.of("status", "ok")
        );
    }

    
}
