package com.seowolseong.board.repository;

import com.seowolseong.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 게시글 조회 리포지토리
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 삭제되지 않은 게시글 최신 50건 조회
     */
    List<Post> findTop50ByDeletedAtIsNullOrderByCreatedAtDescIdDesc();

    /**
     * 전체 게시글 최신 50건 조회 (관리자)
     */
    List<Post> findTop50ByOrderByCreatedAtDescIdDesc();
}
