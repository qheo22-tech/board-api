package com.seowolseong.board.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);
    private static final String RID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "rid";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String rid = httpRequest.getHeader(RID_HEADER);
        if (!StringUtils.hasText(rid)) {
            rid = UUID.randomUUID().toString().split("-")[0];
        }

        try {
            MDC.put(MDC_KEY, rid);
            httpResponse.setHeader(RID_HEADER, rid);

            log.info(">>> [Request] {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

            chain.doFilter(request, response);
            
            log.info("<<< [Response] status: {}", httpResponse.getStatus());
        } finally {
            MDC.clear();
        }
    }
}