package org.example.expert.domain.manager.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    // getManagers
    @Test
    public void manager_목록_조회_시_Todo가_없다면_InvalidRequestException_에러를_던진다() { // 실제로 던지는 예외는 NullPointerException이 아닌 InvalidRequestException이므로 메서드명 수정
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
        assertEquals("Todo not found", exception.getMessage()); // 서비스 로직의 메시지인 "Todo not found"로 수정
    }

    @Test
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        Manager mockManager = new Manager(todo.getUser(), todo);
        List<Manager> managerList = List.of(mockManager);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManager.getId(), managerResponses.get(0).getId());
        assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }

    // saveManager

    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "user", null);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
            managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("일정을 생성한 유저만 담당자를 지정할 수 있습니다.", exception.getMessage());
    }

    @Test
    void saveManager_시_Todo가_없다면_InvalidRequestException_에러를_던진다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.saveManager(authUser, 1L, new ManagerSaveRequest(2L)));
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    void 투두_작성자가_다른_유저이면_담당자_저장_시_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User todoOwner = new User("b@b.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(todoOwner, "id", 99L);
        Todo todo = new Todo("title", "contents", "sunny", todoOwner);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.saveManager(authUser, 1L, new ManagerSaveRequest(2L)));
        assertEquals("일정을 생성한 유저만 담당자를 지정할 수 있습니다.", exception.getMessage());
    }

    @Test
    void 등록하려는_담당자_유저가_존재하지_않으면_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User todoOwner = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(todoOwner, "id", 1L);
        Todo todo = new Todo("title", "contents", "sunny", todoOwner);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.saveManager(authUser, 1L, new ManagerSaveRequest(2L)));
        assertEquals("등록하려고 하는 담당자 유저가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 본인을_담당자로_등록하면_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User todoOwner = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(todoOwner, "id", 1L);
        Todo todo = new Todo("title", "contents", "sunny", todoOwner);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(todoOwner));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.saveManager(authUser, 1L, new ManagerSaveRequest(1L)));
        assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());
    }

    @Test // 테스트코드 샘플
    void todo가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }

    // deleteManager

    @Test
    void deleteManager_시_User가_없다면_InvalidRequestException_에러를_던진다() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(1L, 1L, 1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteManager_시_Todo가_없다면_InvalidRequestException_에러를_던진다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(1L, 1L, 1L));
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    void deleteManager_시_투두_작성자가_null이면_예외가_발생한다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "user", null);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(1L, 1L, 1L));
        assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    void deleteManager_시_투두_작성자와_다른_유저면_예외가_발생한다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        User otherUser = new User("b@b.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(otherUser, "id", 99L);
        Todo todo = new Todo("title", "contents", "sunny", otherUser);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(1L, 1L, 1L));
        assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    void deleteManager_시_Manager가_없다면_InvalidRequestException_에러를_던진다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("title", "contents", "sunny", user);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(1L, 1L, 1L));
        assertEquals("Manager not found", exception.getMessage());
    }

    @Test
    void 해당_투두에_등록된_담당자가_아니면_삭제_시_예외가_발생한다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "contents", "sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Todo otherTodo = new Todo("other", "other", "rainy", user);
        ReflectionTestUtils.setField(otherTodo, "id", 99L);

        User managerUser = new User("b@b.com", "pw", UserRole.USER);
        Manager manager = new Manager(managerUser, otherTodo);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(1L, 1L, 1L));
        assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
    }

    @Test
    void 담당자_삭제가_정상적으로_완료된다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "contents", "sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        User managerUser = new User("b@b.com", "pw", UserRole.USER);
        Manager manager = new Manager(managerUser, todo);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

        // when & then
        assertDoesNotThrow(() -> managerService.deleteManager(1L, 1L, 1L));
        verify(managerRepository).delete(manager);
    }
}
