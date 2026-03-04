package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // 1. request에서 userRole 꺼내기
        String userRole = (String) request.getAttribute("userRole");

        // 2. 어드민 권한 확인
        if (!UserRole.ADMIN.name().equals(userRole)) {
            log.warn("ADMIN API 접근 거부");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            return false;
        }

        // 3. 인증 성공 시 로깅
        log.info("ADMIN API 접근 - 시각: {}, URL: {}", LocalDateTime.now(), request.getRequestURI());
        return true;
    }
}
