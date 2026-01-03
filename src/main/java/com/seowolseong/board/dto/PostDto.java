package com.seowolseong.board.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 게시글 조회 DTO
 */
public record PostDto(
        Long id,
        String title,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        boolean hasFiles,
        List<FileItem> files
) {

    /**
     * 게시글 첨부파일 DTO
     */
    public record FileItem(
            Long id,
            String originalName,
            Long sizeBytes,
            String contentType
    ) {}

    /**
     * 게시글 비밀번호 검증 요청
     */
    public record PostPasswordRequest(
            String postPassword
    ) {}

    /**
     * 게시글 수정 요청
     */
    public record PostUpdateRequest(
            String title,
            String content,
            String postPassword
    ) {}

    /**
     * 게시글 생성 요청
     */
    public record PostCreateRequest(
            String title,
            String content,
            String postPassword
    ) {}
    
    public record AuthUser(
    	    Long id,
    	    String username,
    	    String role
    	) {}

}
