package com.seowolseong.board.dto;

public record AuthDto() {
	
	// ===== 서비스 내부용 (내부 로직에서만 사용) =====
    public record AuthUser(
            Long id,
            String username,
            String role,
            int failedLoginCount
    ) {}

    // ===== HTTP 요청/응답 =====
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(Long id, String username, String role) {}
    public record MeResponse(Long id, String role) {}
    
}
