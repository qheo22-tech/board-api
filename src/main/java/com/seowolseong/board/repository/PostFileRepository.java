package com.seowolseong.board.repository;

import com.seowolseong.board.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    // 특정 게시글에 파일이 하나라도 있는지 (목록에서 "첨부 있음" 표시용)
    boolean existsByPostId(Long postId);

    // 게시글 상세에서 첨부파일 리스트 조회
    List<PostFile> findByPostIdOrderByIdAsc(Long postId);

    // 여러 게시글(목록 50개 등)에 대한 파일 존재 여부를 한 번에 처리할 때 유용
    List<PostFile> findByPostIdIn(Collection<Long> postIds);

    // (선택) 게시글 삭제/정리 시 함께 지우거나 소프트삭제 처리할 때 유용
    long deleteByPostId(Long postId);
}
