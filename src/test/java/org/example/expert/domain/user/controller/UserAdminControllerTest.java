package org.example.expert.domain.user.controller;

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserAdminControllerTest {

    @Mock
    private UserAdminService userAdminService;
    @InjectMocks
    private UserAdminController userAdminController;

    @Test
    void 유저_권한_변경이_정상적으로_완료된다() {
        // given
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");

        // when & then
        assertDoesNotThrow(() -> userAdminController.changeUserRole(1L, request));
        verify(userAdminService).changeUserRole(1L, request);
    }
}
