package org.example.expert.domain.todo.entity;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TodoTest {

    // 엔티티는 Spring 컨텍스트나 Mock 없이 테스트 가능
    // @ExtendWith 없이 @Test만 사용해 비즈니스 로직을 검증
    @Test
    void 투두_내용이_정상적으로_수정된다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        Todo todo = new Todo("기존 제목", "기존 내용", "맑음", user);

        // when
        todo.update("수정된 제목", "수정된 내용");

        // then
        assertEquals("수정된 제목", todo.getTitle());
        assertEquals("수정된 내용", todo.getContents());
    }
}
