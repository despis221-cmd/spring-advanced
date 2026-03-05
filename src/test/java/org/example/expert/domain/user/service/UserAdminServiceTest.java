package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void 존재하지_않는_유저의_권한_변경_시_예외가_발생한다() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userAdminService.changeUserRole(1L, new UserRoleChangeRequest("ADMIN")));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void 유저_권한_변경이_정상적으로_완료된다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when & then
        assertDoesNotThrow(() -> userAdminService.changeUserRole(1L, new UserRoleChangeRequest("ADMIN")));
    }
}
