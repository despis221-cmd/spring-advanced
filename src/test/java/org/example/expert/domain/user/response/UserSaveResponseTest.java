package org.example.expert.domain.user.response;

import org.example.expert.domain.user.dto.response.UserSaveResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserSaveResponseTest {

    @Test
    void UserSaveResponse_생성이_정상적으로_완료된다() {
        // given
        String bearerToken = "bearerToken";

        // when
        UserSaveResponse response = new UserSaveResponse(bearerToken);

        // then
        assertEquals(bearerToken, response.getBearerToken());
    }
}
