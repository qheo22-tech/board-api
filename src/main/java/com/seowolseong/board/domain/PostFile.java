package com.seowolseong.board.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * 게시글 첨부파일 엔티티
 */
@Entity
@Table(name = "post_files")
public class PostFile {

    /**
     * 파일 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 ID
     */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /**
     * 업로드 시 원본 파일명
     */
    @Column(name = "original_name", length = 255, nullable = false)
    private String originalName;

    /**
     * S3 저장 키
     */
    @Column(name = "stored_key", length = 512)
    private String storedKey;

    /**
     * MIME 타입
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 파일 크기 (bytes)
     */
    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    /**
     * 파일 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private FileStatus status;

    /**
     * 실패 사유
     */
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * 수정 시각
     */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * 삭제 시각 (soft delete)
     */
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = FileStatus.PENDING;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // getters
    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public String getOriginalName() { return originalName; }
    public String getStoredKey() { return storedKey; }
    public String getContentType() { return contentType; }
    public Long getSizeBytes() { return sizeBytes; }
    public FileStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }

    // setters
    public void setPostId(Long postId) { this.postId = postId; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public void setStoredKey(String storedKey) { this.storedKey = storedKey; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public void setStatus(FileStatus status) { this.status = status; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }

    /**
     * 파일 삭제 처리 (soft delete)
     */
    public void delete() {
        if (this.status == FileStatus.DELETED) {
            return;
        }
        this.status = FileStatus.DELETED;
        this.deletedAt = OffsetDateTime.now();
    }
}
