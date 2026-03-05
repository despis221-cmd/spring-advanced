package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentAdminControllerTest {

    @Mock
    private CommentAdminService commentAdminService;
    @InjectMocks
    private CommentAdminController commentAdminController;

    @Test
    void 댓글_삭제가_정상적으로_완료된다() {
        // when & then
        assertDoesNotThrow(() -> commentAdminController.deleteComment(1L));
        verify(commentAdminService).deleteComment(1L);
    }
}
