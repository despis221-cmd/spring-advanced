package org.example.expert.domain.user.response.dto;

import org.example.expert.domain.user.dto.response.UserSaveResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserSaveResponseTest {

    // 생성자, @Getter만 있어 테스트할 비즈니스 로직이 없으나 라인 커버리지 100%를 위해 생성자 호출과 getter 동작 검증
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
