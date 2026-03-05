package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void 유저_조회가_정상적으로_완료된다() {
        // given
        User user = new User("a@a.com", "password", UserRole.USER);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(1L);

        // then
        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void 존재하지_않는_유저_조회_시_예외가_발생한다() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.getUser(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void 잘못된_비밀번호로_변경_시_예외가_발생한다() {
        // given
        User user = new User("a@a.com", "encodedPassword", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("wrongPassword", "NewPassword1");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", user.getPassword())).willReturn(false);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(1L, request));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    void 새_비밀번호가_기존_비밀번호와_같으면_예외가_발생한다() {
        // given
        User user = new User("a@a.com", "encodedPassword", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", "NewPassword1");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("oldPassword", user.getPassword())).willReturn(true);
        given(passwordEncoder.matches("NewPassword1", user.getPassword())).willReturn(true);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(1L, request));
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 비밀번호_변경이_정상적으로_완료된다() {
        // given
        User user = new User("a@a.com", "encodedPassword", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", "NewPassword1");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("oldPassword", user.getPassword())).willReturn(true);
        given(passwordEncoder.matches("NewPassword1", user.getPassword())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("newEncodedPassword");

        // when & then
        assertDoesNotThrow(() -> userService.changePassword(1L, request));
    }
}