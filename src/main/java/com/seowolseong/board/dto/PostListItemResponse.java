package com.seowolseong.board.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record PostListItemResponse(
        Long id,
        String title,

        // 상세에서만 사용
        String content,

        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,

        // 목록/상세 공통
        boolean hasFiles,

        // 상세에서만 사용
        List<FileItem> files
) {

    /**
     * 첨부파일 DTO (여러 개 지원)
     * - S3 / 로컬 공통으로 사용 가능
     * - downloadUrl은 필요해질 때 추가
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
}
