package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private WeatherClient weatherClient;
    @InjectMocks
    private TodoService todoService;

    @Test
    void 투두_저장이_정상적으로_완료된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        TodoSaveRequest request = new TodoSaveRequest("title", "contents");
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "맑음", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        given(weatherClient.getTodayWeather()).willReturn("맑음");
        given(todoRepository.save(any(Todo.class))).willReturn(todo);

        // when
        TodoSaveResponse response = todoService.saveTodo(authUser, request);

        // then
        assertNotNull(response);
        assertEquals("title", response.getTitle());
        assertEquals("맑음", response.getWeather());
    }

    @Test
    void 투두_목록_조회가_정상적으로_완료된다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("title", "contents", "맑음", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Page<Todo> todoPage = new PageImpl<>(List.of(todo), PageRequest.of(0, 10), 1);
        given(todoRepository.findAllByOrderByModifiedAtDesc(any())).willReturn(todoPage);

        // when
        Page<TodoResponse> result = todoService.getTodos(1, 10);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals("title", result.getContent().get(0).getTitle());
    }

    @Test
    void 존재하지_않는_투두_단건_조회_시_예외가_발생한다() {
        // given
        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> todoService.getTodo(1L));
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    void 투두_단건_조회가_정상적으로_완료된다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("title", "contents", "맑음", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(1L);

        // then
        assertNotNull(response);
        assertEquals("title", response.getTitle());
    }
}











