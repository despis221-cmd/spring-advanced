package org.example.expert.domain.comment.entity;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentTest {

    @Test
    void 댓글_내용이_정상적으로_수정된다() {
        // given
        User user = new User("a@a.com", "pw", UserRole.USER);
        Todo todo = new Todo("title", "contents", "sunny", user);
        Comment comment = new Comment("기존 내용", user, todo);

        // when
        comment.update("수정된 내용");

        // then
        assertEquals("수정된 내용", comment.getContents());
    }
}
