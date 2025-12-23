package com.seowolseong.board.service;

import com.seowolseong.board.domain.Post;
import com.seowolseong.board.domain.PostFile;
import com.seowolseong.board.dto.PostListItemResponse;
import com.seowolseong.board.repository.PostFileRepository;
import com.seowolseong.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${app.upload-dir}")
    private String uploadDir;

    public PostService(PostRepository postRepository, PostFileRepository postFileRepository) {
        this.postRepository = postRepository;
        this.postFileRepository = postFileRepository;
    }

    /* =========================
    게시글 생성
  ========================= */
 @Transactional
 public Long create(String title, String content, String postPassword,
                    List<MultipartFile> files) throws Exception {

     if (title == null || title.isBlank())
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title required");
     if (content == null || content.isBlank())
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content required");
     if (postPassword == null || postPassword.isBlank())
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "postPassword required");

     Post post = new Post();
     post.setTitle(title.trim());
     post.setContent(content);
     post.setPasswordHash(encoder.encode(postPassword));

     Post saved = postRepository.save(post);

        
        if (files != null && !files.isEmpty()) {
        	//todo s3 연결시 처리
            }
        
        return saved.getId();
    }

    // 파일명 널/경로 주입 방지 최소 처리
    private String safeOriginalName(String original) {
        if (original == null || original.isBlank()) return "file";
        return original.replace("\\", "_").replace("/", "_");
    }
    
    /* =========================
    목록 조회
  ========================= */
 @Transactional(readOnly = true)
 public List<PostListItemResponse> list() {
     return postRepository
             .findTop50ByDeletedAtIsNullOrderByCreatedAtDescIdDesc()
             .stream()
             .map(p -> new PostListItemResponse(
                     p.getId(),
                     p.getTitle(),
                     null,
                     p.getCreatedAt(),
                     null,
                     postFileRepository.existsByPostId(p.getId()),
                     null
             ))
             .toList();
 }

 /* =========================
    상세 조회
  ========================= */
 @Transactional(readOnly = true)
 public PostListItemResponse detail(Long id) {

     Post p = findActivePostOrThrow(id);

     var files = postFileRepository.findByPostIdOrderByIdAsc(id)
             .stream()
             .map(f -> new PostListItemResponse.FileItem(
                     f.getId(),
                     f.getOriginalName(),
                     f.getSizeBytes(),
                     f.getContentType()
             ))
             .toList();

     return new PostListItemResponse(
             p.getId(),
             p.getTitle(),
             p.getContent(),
             p.getCreatedAt(),
             p.getUpdatedAt(),
             !files.isEmpty(),
             files
     );
 }

 /* =========================
    게시글 삭제
  ========================= */
 @Transactional
 public void deletePost(Long id, String postPassword) {

     Post p = findActivePostOrThrow(id);
     verifyPasswordOrThrow(p, postPassword);

     p.setDeletedAt(OffsetDateTime.now());
     // save() 필요 없음 (dirty checking)
 }

 /* =========================
    게시글 수정
  ========================= */
 @Transactional
 public void updatePost(Long id, String title, String content, String postPassword) {

     Post p = findActivePostOrThrow(id);
     verifyPasswordOrThrow(p, postPassword);

     if (title == null || title.isBlank())
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title required");
     if (content == null || content.isBlank())
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content required");

     p.setTitle(title.trim());
     p.setContent(content);
 }
 
 
 @Transactional(readOnly = true)
 public void verifyPasswordOrThrow(Long id, String postPassword) {
     Post p = findActivePostOrThrow(id);
     verifyPasswordOrThrow(p, postPassword);
 }


 /* =========================
    내부 공통 로직
  ========================= */

 private Post findActivePostOrThrow(Long id) {
     Post p = postRepository.findById(id)
             .orElseThrow(() ->
                     new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.")
             );

     if (p.getDeletedAt() != null) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 삭제된 게시글입니다.");
     }
     return p;
 }

 private void verifyPasswordOrThrow(Post p, String postPassword) {
     if (postPassword == null || postPassword.isBlank()) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호를 입력하세요.");
     }
     if (!encoder.matches(postPassword, p.getPasswordHash())) {
         throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 틀립니다.");
     }
 }
 
}


   
