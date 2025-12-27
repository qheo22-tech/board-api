package com.seowolseong.board.error;

/**
 * API 에러 코드 정의
 *
 * - name(): 프론트 분기용 에러 코드
 * - status(): HTTP 상태 코드
 * - defaultMessage(): 기본 응답 메시지
 */
public enum ErrorCode {

    /* ---------- Common ---------- */
    BAD_REQUEST(400, "잘못된 요청입니다."),
    INVALID_INPUT(400, "입력값이 올바르지 않습니다."),
    REQUIRED_FIELD_MISSING(400, "필수 값이 누락되었습니다."),
    METHOD_NOT_ALLOWED(405, "허용되지 않은 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "지원하지 않는 Content-Type 입니다."),
    REQUEST_BODY_MISSING(400, "요청 본문이 비어 있습니다."),
    JSON_PARSE_ERROR(400, "요청 JSON 형식이 올바르지 않습니다."),

    /* ---------- Post ---------- */
    POST_NOT_FOUND(404, "게시글이 없습니다."),
    POST_ALREADY_DELETED(404, "이미 삭제된 게시글입니다."),
    POST_TITLE_REQUIRED(400, "제목을 입력하세요."),
    POST_CONTENT_REQUIRED(400, "내용을 입력하세요."),
    POST_PASSWORD_REQUIRED(400, "비밀번호를 입력하세요."),
    POST_PASSWORD_MISMATCH(403, "비밀번호가 틀립니다."),
    POST_PASSWORD_VERIFY_FAILED(403, "비밀번호 검증에 실패했습니다."),
    POST_CREATE_FAILED(500, "게시글 생성에 실패했습니다."),
    POST_UPDATE_FAILED(500, "게시글 수정에 실패했습니다."),
    POST_DELETE_FAILED(500, "게시글 삭제에 실패했습니다."),
    POST_READ_FAILED(500, "게시글 조회에 실패했습니다."),

    /* ---------- File ---------- */
    FILE_NOT_FOUND(404, "파일이 없습니다."),
    FILE_ALREADY_DELETED(404, "이미 삭제된 파일입니다."),
    FILE_NOT_READY(409, "파일이 아직 준비되지 않았습니다."),
    FILE_FAILED(409, "파일 처리에 실패했습니다."),
    FILE_STATUS_INVALID(409, "파일 상태가 올바르지 않습니다."),
    FILE_UPLOAD_EMPTY(400, "업로드할 파일이 없습니다."),
    FILE_UPLOAD_TOO_LARGE(413, "파일 크기가 너무 큽니다."),
    FILE_CONTENT_TYPE_NOT_ALLOWED(415, "허용되지 않은 파일 형식입니다."),
    FILE_NAME_INVALID(400, "파일명이 올바르지 않습니다."),
    FILE_LIST_FAILED(500, "첨부파일 목록 조회에 실패했습니다."),
    FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다."),
    FILE_DOWNLOAD_FAILED(500, "파일 다운로드에 실패했습니다."),
    FILE_DELETE_FAILED(500, "파일 삭제에 실패했습니다."),
    

    /* ---------- Storage ---------- */
    STORAGE_UPLOAD_FAILED(502, "저장소 업로드에 실패했습니다."),
    STORAGE_DOWNLOAD_FAILED(502, "저장소 다운로드에 실패했습니다."),
    STORAGE_DELETE_FAILED(502, "저장소 삭제에 실패했습니다."),
    STORAGE_IO_ERROR(500, "파일 처리 중 오류가 발생했습니다."),
    S3_ERROR(502, "S3 처리 중 오류가 발생했습니다."),
    S3_BUCKET_NOT_CONFIGURED(500, "S3 버킷 설정이 누락되었습니다."),
    S3_KEY_INVALID(400, "S3 객체 키가 올바르지 않습니다."),
    LOCAL_STORAGE_PATH_INVALID(500, "로컬 저장 경로 설정이 올바르지 않습니다."),
    LOCAL_FILE_NOT_FOUND(404, "로컬 파일이 없습니다."),

    /* ---------- Auth / Access ---------- */
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),

    /* ---------- System ---------- */
    INTERNAL_ERROR(500, "서버 오류가 발생했습니다."),
    NOT_IMPLEMENTED(501, "아직 구현되지 않았습니다.");

    private final int status;
    private final String defaultMessage;

    ErrorCode(int status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public int status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
