package com.seowolseong.board.repository;

import com.seowolseong.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 삭제 안 된 글 최신순 50개
    List<Post> findTop50ByDeletedAtIsNullOrderByCreatedAtDescIdDesc();
}
