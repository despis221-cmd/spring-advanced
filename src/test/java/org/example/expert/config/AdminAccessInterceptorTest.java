package org.example.expert.config;

import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminAccessInterceptorTest {

    @InjectMocks
    private AdminAccessInterceptor interceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void ADMIN_권한이면_preHandle이_true를_반환한다() throws Exception {
        // given
        request.setAttribute("userRole", UserRole.ADMIN.name());

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
        assertEquals(200, response.getStatus());
    }

    @Test
    void ADMIN_권한이_아니면_preHandle이_false를_반환하고_403을_응답한다() throws Exception {
        // given
        request.setAttribute("userRole", UserRole.USER.name());

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    void userRole이_null이면_preHandle이_false를_반환하고_403을_응답한다() throws Exception {
        // given: userRole 속성 없음
        // JwtFilter를 거치지 않은 요청이나 attribute 누락 케이스를 방어하기 위한 테스트

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }
}
