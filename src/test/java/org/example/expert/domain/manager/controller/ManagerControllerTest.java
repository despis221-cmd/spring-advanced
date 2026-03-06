package org.example.expert.domain.manager.controller;

import io.jsonwebtoken.Claims;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {

    @Mock
    private ManagerService managerService;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private ManagerController managerController;
    @Mock
    private Claims claims;

    @Test
    void 담당자_저장이_정상적으로_완료된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        ManagerSaveRequest request = new ManagerSaveRequest(2L);
        ManagerSaveResponse response = new ManagerSaveResponse(1L, new UserResponse(2L, "b@b.com"));

        given(managerService.saveManager(any(AuthUser.class), anyLong(), any(ManagerSaveRequest.class)))
                .willReturn(response);

        // when
        ResponseEntity<ManagerSaveResponse> result = managerController.saveManager(authUser, 1L, request);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals(2L, result.getBody().getUser().getId());
        assertEquals("b@b.com", result.getBody().getUser().getEmail());
    }

    @Test
    void 담당자_목록_조회가_정상적으로_완료된다() {
        // given
        ManagerResponse managerResponse = new ManagerResponse(1L, new UserResponse(2L, "b@b.com"));
        given(managerService.getManagers(anyLong())).willReturn(List.of(managerResponse));

        // when
        ResponseEntity<List<ManagerResponse>> result = managerController.getMembers(1L);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(1L, result.getBody().get(0).getId());
        assertEquals(2L, result.getBody().get(0).getUser().getId());
        assertEquals("b@b.com", result.getBody().get(0).getUser().getEmail());
    }

    @Test
    void 담당자_삭제가_정상적으로_완료된다() {
        // given
        given(jwtUtil.extractClaims(anyString())).willReturn(claims);
        given(claims.getSubject()).willReturn("1");

        // when
        managerController.deleteManager("Bearer token", 1L, 1L);

        // then
        verify(managerService).deleteManager(1L, 1L, 1L);
    }
}