package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminApiLoggingAopTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @InjectMocks
    private AdminApiLoggingAop aop;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setRequestURI("/admin/test");
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void logAdminApi가_정상적으로_실행되고_결과를_반환한다() throws Throwable {
        // given
        request.setAttribute("userId", 1L);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        given(joinPoint.getArgs()).willReturn(new Object[]{"arg1"});
        given(objectMapper.writeValueAsString(joinPoint.getArgs())).willReturn("[\"arg1\"]");
        given(joinPoint.proceed()).willReturn("response");
        given(objectMapper.writeValueAsString("response")).willReturn("\"response\"");

        // when
        Object result = aop.logAdminApi(joinPoint);

        // then
        assertEquals("response", result);
        verify(joinPoint).proceed();
    }

    @Test
    void logAdminApi_userId가_null이어도_정상적으로_실행된다() throws Throwable {
        // given
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        given(joinPoint.getArgs()).willReturn(new Object[]{});
        given(objectMapper.writeValueAsString(joinPoint.getArgs())).willReturn("[]");
        given(joinPoint.proceed()).willReturn(null);
        given(objectMapper.writeValueAsString(null)).willReturn("null");

        // when
        Object result = aop.logAdminApi(joinPoint);

        // then
        assertEquals(null, result);
        verify(joinPoint).proceed();
    }
}