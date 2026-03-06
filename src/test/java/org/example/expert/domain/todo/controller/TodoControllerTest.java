package org.example.expert.domain.todo.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    @Mock
    private TodoService todoService;
    @InjectMocks
    private TodoController todoController;

    @Test
    void 투두_저장이_정상적으로_완료된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        TodoSaveRequest request = new TodoSaveRequest("title", "contents");
        TodoSaveResponse response = new TodoSaveResponse(1L, "title", "contents", "맑음", new UserResponse(1L, "a@a.com"));

        given(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class))).willReturn(response);

        // when
        ResponseEntity<TodoSaveResponse> result = todoController.saveTodo(authUser, request);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("title", result.getBody().getTitle());
    }

    @Test
    void 투두_목록_조회가_정상적으로_완료된다() {
        // given
        TodoResponse todoResponse = new TodoResponse(1L, "title", "contents", "맑음", new UserResponse(1L, "a@a.com"), null, null);
        // 임의값 사용 시 테스트 불안정해지므로 null로 설정
        Page<TodoResponse> page = new PageImpl<>(List.of(todoResponse));

        given(todoService.getTodos(anyInt(), anyInt())).willReturn(page);

        // when
        ResponseEntity<Page<TodoResponse>> result = todoController.getTodos(1, 10);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void 투두_단건_조회가_정상적으로_완료된다() {
        // given
        TodoResponse todoResponse = new TodoResponse(1L, "title", "contents", "맑음", new UserResponse(1L, "a@a.com"), null, null);

        given(todoService.getTodo(anyLong())).willReturn(todoResponse);

        // when
        ResponseEntity<TodoResponse> result = todoController.getTodo(1L);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("title", result.getBody().getTitle());
    }
}
