package com.seowolseong.board.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * 게시글 도메인 엔티티
 *
 * - DB의 posts 테이블과 1:1 매핑
 * - 게시글의 상태를 JPA가 관리하는 영속 객체
 */
@Entity
@Table(name = "posts")
public class Post {

    /**
     * 게시글 PK
     * - DB 자동 증가 값
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 제목
     * - 목록/상세 화면에 노출
     */
    @Column(length = 200, nullable = false)
    private String title;

    /**
     * 게시글 본문
     * - 길이 제한 없음
     */
    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * 게시글 비밀번호 해시값
     * - 원문 비밀번호는 저장하지 않음
     */
    @Column(name = "password_hash", length = 100, nullable = false)
    private String passwordHash;

    /**
     * 생성 시각
     * - 최초 저장 시 자동 설정
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * 수정 시각
     * - 엔티티 수정 시 자동 갱신
     */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * 삭제 시각 (소프트 삭제)
     * - null이면 정상 데이터
     * - 값이 있으면 삭제된 데이터
     */
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    /**
     * 최초 저장 시 생성/수정 시각 자동 세팅
     */
    @PrePersist
    void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * 엔티티 수정 시 수정 시각 자동 갱신
     */
    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // getters/setters
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }

    // 필요 시 사용 (테스트/관리자 기능 등)
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }
}
