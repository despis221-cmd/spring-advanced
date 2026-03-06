package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;
    @InjectMocks
    private CommentController commentController;

    @Test
    void 댓글_저장이_정상적으로_완료된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        CommentSaveRequest request = new CommentSaveRequest("contents");
        CommentSaveResponse response = new CommentSaveResponse(1L, "contents", new UserResponse(1L, "a@a.com"));

        given(commentService.saveComment(any(AuthUser.class), anyLong(), any(CommentSaveRequest.class))) // 객체 동일성에 의존하지 않도록 설정
                .willReturn(response);

        // when
        ResponseEntity<CommentSaveResponse> result = commentController.saveComment(authUser, 1L, request);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("contents", result.getBody().getContents());
    }

    @Test
    void 댓글_목록_조회가_정상적으로_완료된다() {
        // given
        CommentResponse commentResponse = new CommentResponse(1L, "contents", new UserResponse(1L, "a@a.com"));
        given(commentService.getComments(anyLong())).willReturn(List.of(commentResponse));

        // when
        ResponseEntity<List<CommentResponse>> result = commentController.getComments(1L);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }
}
