package org.example.expert.domain.auth.controller;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    void 회원가입이_정상적으로_완료된다() {
        // given
        SignupRequest request = new SignupRequest("a@a.com", "Password1!", "USER");
        SignupResponse response = new SignupResponse("bearerToken");
        given(authService.signup(any(SignupRequest.class))).willReturn(response);

        // when
        SignupResponse result = authController.signup(request);

        // then
        assertNotNull(result);
        assertEquals("bearerToken", result.getBearerToken());
    }

    @Test
    void 로그인이_정상적으로_완료된다() {
        // given
        SigninRequest request = new SigninRequest("a@a.com", "Password1!");
        SigninResponse response = new SigninResponse("bearerToken");
        given(authService.signin(any(SigninRequest.class))).willReturn(response);

        // when
        SigninResponse result = authController.signin(request);

        // then
        assertNotNull(result);
        assertEquals("bearerToken", result.getBearerToken());
    }
}
