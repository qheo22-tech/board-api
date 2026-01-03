package com.seowolseong.board.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.seowolseong.board.common.SessionKeys;
import com.seowolseong.board.dto.AuthDto.LoginRequest;
import com.seowolseong.board.dto.AuthDto.LoginResponse;
import com.seowolseong.board.dto.AuthDto.MeResponse;
import com.seowolseong.board.dto.PostDto.AuthUser;
import com.seowolseong.board.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 로그인 (세션 생성)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpSession session) {
        AuthUser user = authService.authenticate(req.username(), req.password());

        session.setAttribute(SessionKeys.LOGIN_USER_ID, user.id());
        session.setAttribute(SessionKeys.ROLE, user.role()); // "ADMIN" or "USER"

        return ResponseEntity.ok(new LoginResponse(user.id(), user.username(), user.role()));
    }


    //  로그인 상태 확인용 (프론트에서 버튼 조건부 렌더링에 사용)
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // ⭐ 없으면 만들지 않음
        if (session == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = (Long) session.getAttribute(SessionKeys.LOGIN_USER_ID);
        String role = (String) session.getAttribute(SessionKeys.ROLE);

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(new MeResponse(userId, role));
    }


}
