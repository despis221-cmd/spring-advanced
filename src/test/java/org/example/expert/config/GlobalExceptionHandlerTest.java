package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void InvalidRequestException_발생_시_400을_반환한다() {
        // given
        InvalidRequestException ex = new InvalidRequestException("잘못된 요청");

        // when
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.invalidRequestExceptionException(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("잘못된 요청", response.getBody().get("message"));
    }

    @Test
    void AuthException_발생_시_401을_반환한다() {
        // given
        AuthException ex = new AuthException("인증 실패");

        // when
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleAuthException(ex);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("인증 실패", response.getBody().get("message"));
    }

    @Test
    void ServerException_발생_시_500을_반환한다() {
        // given
        ServerException ex = new ServerException("서버 오류");

        // when
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleServerException(ex);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("서버 오류", response.getBody().get("message"));
    }
}
