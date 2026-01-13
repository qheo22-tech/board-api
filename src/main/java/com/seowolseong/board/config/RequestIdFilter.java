package com.seowolseong.board.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter implements Filter {

    private static final String RID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "rid";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Nginx 헤더에서 RID 추출 (없으면 생성)
        String rid = httpRequest.getHeader(RID_HEADER);
        if (!StringUtils.hasText(rid)) {
            rid = UUID.randomUUID().toString().split("-")[0]; // 8자리만 사용
        }

        try {
            // 2. 로그 패턴(%X{rid})에 값 주입
            MDC.put(MDC_KEY, rid);

            // 3. 응답 헤더에도 RID 추가 (클라이언트 추적용)
            // 브라우저 개발자도구 Network 탭에서 바로 확인 가능하게 함
            httpResponse.setHeader(RID_HEADER, rid);

            chain.doFilter(request, response);
        } finally {
            // 4. 반드시 Clear (ThreadLocal 방식이라 안 비우면 다른 요청 로그에 섞임)
            MDC.clear();
        }
    }
}