package com.seowolseong.board.api;

import com.seowolseong.board.dto.PostDto;
import com.seowolseong.board.dto.PostDto.PostCreateRequest;
import com.seowolseong.board.dto.PostDto.PostPasswordRequest;
import com.seowolseong.board.dto.PostDto.PostUpdateRequest;
import com.seowolseong.board.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 게시글 API
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
     */
    @GetMapping
    public List<PostDto> list() {
        return postService.list();
    }

    /**
     * 게시글 상세 조회
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public PostDto detail(@PathVariable Long id) {
        return postService.detail(id);
    }

    /**
     * 게시글 생성
     * POST /api/posts
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Long>> createPost(@RequestBody PostCreateRequest req) {
        Long postId = postService.create(req.title(), req.content(), req.postPassword());
        return ResponseEntity.ok(Map.of("id", postId));
    }

    /**
     * 게시글 비밀번호 검증
     * POST /api/posts/{id}/verify-password
     */
    @PostMapping("/{id}/verify-password")
    public ResponseEntity<Map<String, Boolean>> verifyPassword(
            @PathVariable Long id,
            @RequestBody PostPasswordRequest req
    ) {
        postService.verifyPasswordOrThrow(id, req.postPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * 게시글 삭제 (soft delete)
     * POST /api/posts/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<Map<String, Boolean>> delete(
            @PathVariable Long id,
            @RequestBody PostPasswordRequest req
    ) {
        postService.deletePost(id, req.postPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * 게시글 수정
     * POST /api/posts/{id}/update
     */
    @PostMapping("/{id}/update")
    public ResponseEntity<Map<String, Boolean>> update(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest req
    ) {
        postService.updatePost(id, req.title(), req.content(), req.postPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * 헬스체크
     * GET /api/posts/ping
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
