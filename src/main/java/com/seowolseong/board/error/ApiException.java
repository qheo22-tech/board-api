package com.seowolseong.board.error;

/**
 * 애플리케이션 공통 API 예외
 */
public class ApiException extends RuntimeException {

    private final ErrorCode code;

    /**
     * ErrorCode 기본 메시지를 사용하는 예외
     */
    public ApiException(ErrorCode code) {
        super(code.defaultMessage());
        this.code = code;
    }

    /**
     * 메시지를 직접 지정하는 예외
     */
    public ApiException(ErrorCode code, String messageOverride) {
        super(messageOverride);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
