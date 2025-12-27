package com.seowolseong.board.service.model;

import java.io.InputStream;

/**
 * 파일 다운로드용 리소스 묶음
 */
public record FileDownloadResource(
        InputStream inputStream,
        String originalName,
        String contentType,
        Long sizeBytes
) {}
