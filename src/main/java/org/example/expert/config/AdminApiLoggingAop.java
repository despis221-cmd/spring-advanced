package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminApiLoggingAop {

    private final ObjectMapper objectMapper;

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))")
    public Object logAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Long userId = (Long) request.getAttribute("userId");
        String requestUrl = request.getRequestURI();

        String requestBody = objectMapper.writeValueAsString(joinPoint.getArgs());
        log.info("ADMIN API 요청 - 사용자 ID: {}, 요청 시각: {}, URL: {}, 요청 본문: {}", userId, LocalDateTime.now(), requestUrl, requestBody);

        Object result = joinPoint.proceed();

        String responseBody = objectMapper.writeValueAsString(result);
        log.info("ADMIN API 응답 - 응답 본문: {}", responseBody);

        return result;
    }
}
