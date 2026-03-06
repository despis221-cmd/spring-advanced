package org.example.expert.config;

import io.jsonwebtoken.Claims;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // 실제 운영 키 노출 방지
        ReflectionTestUtils.setField(jwtUtil, "secretKey",
                "dGVzdFNlY3JldEtleUZvckp3dFRlc3RpbmdQdXJwb3Nl");
        jwtUtil.init();
    }

    @Test
    void 토큰이_정상적으로_생성된다() {
        // when
        String token = jwtUtil.createToken(1L, "a@a.com", UserRole.USER);

        // then
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    void substringToken이_정상적으로_동작한다() {
        // given
        String token = jwtUtil.createToken(1L, "a@a.com", UserRole.USER);

        // when
        String result = jwtUtil.substringToken(token);

        // then
        assertNotNull(result);
        assertFalse(result.startsWith("Bearer "));
    }

    @Test
    void Bearer_prefix가_없으면_예외가_발생한다() {
        // when & then
        ServerException exception = assertThrows(ServerException.class,
                () -> jwtUtil.substringToken("invalidToken"));
        assertEquals("Not Found Token", exception.getMessage());
    }

    @Test
    void extractClaims가_정상적으로_동작한다() {
        // given
        String token = jwtUtil.createToken(1L, "a@a.com", UserRole.USER);
        String jwt = jwtUtil.substringToken(token);

        // when
        Claims claims = jwtUtil.extractClaims(jwt);

        // then
        assertNotNull(claims);
        assertEquals("1", claims.getSubject());
        assertEquals("a@a.com", claims.get("email"));
    }

    @Test
    void token이_null이면_예외가_발생한다() {
        // when & then
        ServerException exception = assertThrows(ServerException.class,
                () -> jwtUtil.substringToken(null));
        assertEquals("Not Found Token", exception.getMessage());
    }
}
