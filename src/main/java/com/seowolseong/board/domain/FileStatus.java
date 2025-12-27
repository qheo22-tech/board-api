package com.seowolseong.board.domain;

/**
 * 파일 업로드 상태
 *
 * - PENDING : 업로드 시도 기록됨 (아직 S3 미완료)
 * - READY   : S3 업로드 성공
 * - FAILED  : 업로드 실패
 * - DELETED : (선택) 논리 삭제
 */
public enum FileStatus {
    PENDING,
    READY,
    FAILED,
    DELETED
}
