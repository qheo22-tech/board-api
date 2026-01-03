package com.seowolseong.board.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "role", nullable = false)
    private String role;   

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    protected Admin() {}

    // ===== 도메인 행위 =====
    public void increaseFailedLoginCount() {
        this.failedLoginCount++;
    }

    public void resetFailedLoginCount() {
        this.failedLoginCount = 0;
    }

    public void markLoginSuccess() {
        this.lastLoginAt = OffsetDateTime.now();
    }

    public boolean isLocked() {
        return failedLoginCount >= 5;
    }

    // ===== getter만 =====
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getRole() { return role; }         
    public int getFailedLoginCount() { return failedLoginCount; }
    public OffsetDateTime getLastLoginAt() { return lastLoginAt; }
}

