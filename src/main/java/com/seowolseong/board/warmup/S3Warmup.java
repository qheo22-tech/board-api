package com.seowolseong.board.warmup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Component
public class S3Warmup {

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    public S3Warmup(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * 애플리케이션 기동 시 S3 연결 워밍업
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        long t0 = System.currentTimeMillis();

        try {
            s3Client.headBucket(b -> b.bucket(bucket));
        } catch (Exception e) {
            // 워밍업 실패 시 로그만 남김
        }
    }
}
