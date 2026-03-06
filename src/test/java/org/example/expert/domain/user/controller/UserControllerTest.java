package org.example.expert.domain.user.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void 유저_단건_조회가_정상적으로_완료된다() {
        // given
        UserResponse response = new UserResponse(1L, "a@a.com");
        given(userService.getUser(anyLong())).willReturn(response);

        // when
        ResponseEntity<UserResponse> result = userController.getUser(1L);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals("a@a.com", result.getBody().getEmail());
    }

    @Test
    void 비밀번호_변경이_정상적으로_완료된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("OldPass1!", "NewPass1!");
        doNothing().when(userService).changePassword(anyLong(), any(UserChangePasswordRequest.class));

        // when & then
        assertDoesNotThrow(() -> userController.changePassword(authUser, request));
    }
}
