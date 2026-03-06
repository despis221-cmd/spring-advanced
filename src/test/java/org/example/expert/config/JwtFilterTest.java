package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private FilterChain filterChain;
    @Mock
    private Claims claims;

    private JwtFilter jwtFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        jwtFilter = new JwtFilter(jwtUtil, new ObjectMapper());
    }

    @Test
    void auth_경로는_필터를_통과한다() throws Exception {
        // given
        request.setRequestURI("/auth/signin");

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void Authorization_헤더가_없으면_401을_반환한다() throws Exception {
        // given
        request.setRequestURI("/todos");

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void 유효한_토큰으로_정상적으로_통과한다() throws Exception {
        // given
        request.setRequestURI("/todos");
        request.addHeader("Authorization", "Bearer validToken");

        given(jwtUtil.substringToken(anyString())).willReturn("validToken");
        given(jwtUtil.extractClaims("validToken")).willReturn(claims);
        given(claims.get("userRole", String.class)).willReturn("USER");
        given(claims.getSubject()).willReturn("1");
        given(claims.get("email")).willReturn("a@a.com");
        given(claims.get("userRole")).willReturn("USER");

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void claims가_null이면_401을_반환한다() throws Exception {
        // given
        request.setRequestURI("/todos");
        request.addHeader("Authorization", "Bearer validToken");

        given(jwtUtil.substringToken(anyString())).willReturn("validToken");
        given(jwtUtil.extractClaims("validToken")).willReturn(null);

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void admin_경로에_USER_권한으로_접근하면_403을_반환한다() throws Exception {
        // given
        request.setRequestURI("/admin/users");
        request.addHeader("Authorization", "Bearer validToken");

        given(jwtUtil.substringToken(anyString())).willReturn("validToken");
        given(jwtUtil.extractClaims("validToken")).willReturn(claims);
        given(claims.get("userRole", String.class)).willReturn("USER");
        given(claims.getSubject()).willReturn("1");
        given(claims.get("email")).willReturn("a@a.com");
        given(claims.get("userRole")).willReturn("USER");

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void 만료된_토큰이면_401을_반환한다() throws Exception {
        // given
        request.setRequestURI("/todos");
        request.addHeader("Authorization", "Bearer expiredToken");

        given(jwtUtil.substringToken(anyString())).willReturn("expiredToken");
        given(jwtUtil.extractClaims("expiredToken")).willThrow(
                new io.jsonwebtoken.ExpiredJwtException(null, claims, "expired")
        );

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void 잘못된_토큰이면_400을_반환한다() throws Exception {
        // given
        request.setRequestURI("/todos");
        request.addHeader("Authorization", "Bearer malformedToken");

        given(jwtUtil.substringToken(anyString())).willReturn("malformedToken");
        given(jwtUtil.extractClaims("malformedToken")).willThrow(
                new io.jsonwebtoken.MalformedJwtException("malformed")
        );

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    void 예상치_못한_예외가_발생하면_500을_반환한다() throws Exception {
        // given
        request.setRequestURI("/todos");
        request.addHeader("Authorization", "Bearer errorToken");

        given(jwtUtil.substringToken(anyString())).willReturn("errorToken");
        given(jwtUtil.extractClaims("errorToken")).willThrow(new RuntimeException("unexpected"));

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    void admin_경로에_ADMIN_권한으로_접근하면_통과한다() throws Exception {
        // given
        request.setRequestURI("/admin/users");
        request.addHeader("Authorization", "Bearer validToken");

        given(jwtUtil.substringToken(anyString())).willReturn("validToken");
        given(jwtUtil.extractClaims("validToken")).willReturn(claims);
        given(claims.get("userRole", String.class)).willReturn("ADMIN");
        given(claims.getSubject()).willReturn("1");
        given(claims.get("email")).willReturn("a@a.com");
        given(claims.get("userRole")).willReturn("ADMIN");

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }
}
