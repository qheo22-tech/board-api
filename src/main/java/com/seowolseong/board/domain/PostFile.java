package com.seowolseong.board.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * 게시글 첨부파일 도메인 엔티티
 *
 * - 게시글(Post)에 속한 첨부파일 메타데이터를 관리
 * - 실제 파일 바이너리는 DB에 저장하지 않음
 *   (로컬 파일 시스템 / S3 등에 저장)
 */
@Entity
@Table(name = "post_files")
public class PostFile {

    /**
     * 첨부파일 PK
     * - DB 자동 증가 값
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 첨부파일이 속한 게시글 ID
     * - posts.id 와 매핑되는 FK 역할
     * - 단순 FK(Long)로 관리 (연관관계 매핑 생략)
     */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /**
     * 사용자가 업로드한 원본 파일명
     * - 화면 표시용
     */
    @Column(name = "original_name", length = 255, nullable = false)
    private String originalName;

    /**
     * 저장소 내부에서 사용하는 파일 식별자
     * - 로컬: 실제 저장된 파일명
     * - S3: 객체 key 값
     */
    @Column(name = "stored_name", length = 255, nullable = false)
    private String storedName;

    /**
     * 파일 Content-Type
     * - 예: image/png, application/pdf
     * - 다운로드/미리보기 시 활용
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 파일 크기 (bytes)
     * - 목록/상세 화면 표시용
     */
    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    /**
     * 파일 업로드 시각
     * - 최초 저장 시 자동 설정
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * 엔티티 최초 저장 시 업로드 시각 자동 세팅
     */
    @PrePersist
    void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }

    // ===== getters (조회용) =====
    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public String getOriginalName() { return originalName; }
    public String getStoredName() { return storedName; }
    public String getContentType() { return contentType; }
    public Long getSizeBytes() { return sizeBytes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    // ===== setters (저장/생성 시 사용) =====
    public void setPostId(Long postId) { this.postId = postId; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
}
