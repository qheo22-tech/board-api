package com.seowolseong.board.repository;

import com.seowolseong.board.domain.FileStatus;
import com.seowolseong.board.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    /* ---------- Exists ---------- */
    boolean existsByPostId(Long postId);
    boolean existsByPostIdAndDeletedAtIsNull(Long postId);

    /* ---------- Find (single post) ---------- */
    List<PostFile> findByPostIdOrderByIdAsc(Long postId);
    List<PostFile> findByPostIdAndDeletedAtIsNullOrderByIdAsc(Long postId);
    List<PostFile> findByPostIdAndDeletedAtIsNullOrderByIdDesc(Long postId);
    List<PostFile> findByPostIdAndStatusAndDeletedAtIsNull(Long postId, FileStatus status);

    /* ---------- Find (multiple posts) ---------- */
    List<PostFile> findByPostIdIn(Collection<Long> postIds);
    List<PostFile> findByPostIdInAndDeletedAtIsNull(Collection<Long> postIds);

    /* ---------- Delete ---------- */
    long deleteByPostId(Long postId);

    /* ---------- Soft delete ---------- */

    /* ---------- Soft delete ---------- */

    /**
     * 파일 삭제 (status=DELETED, deletedAt 설정)
     */
    @Modifying
    @Query("""
        update PostFile f
           set f.status = com.seowolseong.board.domain.FileStatus.DELETED,
               f.deletedAt = CURRENT_TIMESTAMP,
               f.updatedAt = CURRENT_TIMESTAMP
         where f.id = :fileId
           and f.deletedAt is null
    """)
    int softDeleteById(Long fileId);

    /**
     * 게시글 파일 전체 삭제 (status=DELETED, deletedAt 설정)
     */
    @Modifying
    @Query("""
        update PostFile f
           set f.status = com.seowolseong.board.domain.FileStatus.DELETED,
               f.deletedAt = CURRENT_TIMESTAMP,
               f.updatedAt = CURRENT_TIMESTAMP
         where f.postId = :postId
           and f.deletedAt is null
    """)
    int softDeleteByPostId(Long postId);

}
