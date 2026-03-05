package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthUserArgumentResolverTest {

    @InjectMocks
    private AuthUserArgumentResolver resolver;

    // 테스트용 더미 컨트롤러
    static class TestController {
        public void withAuthAndAuthUser(@Auth AuthUser authUser) {}
        public void withAuthUserOnly(AuthUser authUser) {}
        public void withStringOnly(String str) {}
        public void withAuthAndString(@Auth String str) {}
    }

    @Test
    void Auth_어노테이션과_AuthUser_타입이_모두_있으면_true를_반환한다() throws Exception {
        // given
        Method method = TestController.class.getMethod("withAuthAndAuthUser", AuthUser.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // when & then
        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    void Auth_어노테이션도_없고_AuthUser_타입도_아니면_false를_반환한다() throws Exception {
        // given
        Method method = TestController.class.getMethod("withStringOnly", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // when & then
        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    void Auth_어노테이션이_없고_AuthUser_타입만_있으면_예외가_발생한다() throws Exception {
        // given
        Method method = TestController.class.getMethod("withAuthUserOnly", AuthUser.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // when & then
        assertThrows(AuthException.class, () -> resolver.supportsParameter(parameter));
    }

    @Test
    void Auth_어노테이션만_있고_AuthUser_타입이_아니면_예외가_발생한다() throws Exception {
        // given
        Method method = TestController.class.getMethod("withAuthAndString", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // when & then
        assertThrows(AuthException.class, () -> resolver.supportsParameter(parameter));
    }

    @Test
    void resolveArgument가_AuthUser를_정상적으로_반환한다() throws Exception {
        // given
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setAttribute("userId", 1L);
        httpRequest.setAttribute("email", "a@a.com");
        httpRequest.setAttribute("userRole", "USER");

        // when
        Object result = resolver.resolveArgument(null, null, new ServletWebRequest(httpRequest), null);

        // then
        assertNotNull(result);
        assertInstanceOf(AuthUser.class, result);
        AuthUser authUser = (AuthUser) result;
        assertEquals(1L, authUser.getId());
        assertEquals("a@a.com", authUser.getEmail());
        assertEquals(UserRole.USER, authUser.getUserRole());
    }
}