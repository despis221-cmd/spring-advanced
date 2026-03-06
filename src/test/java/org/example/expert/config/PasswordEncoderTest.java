package org.example.expert.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderTest {

    // Spring 컨텍스트 없이 PasswordEncoder를 직접 생성
    // Mock이나 @ExtendWith 불필요
    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @Test
    @DisplayName("비밀번호가 올바르게 인코딩되고 matches() 메서드가 정상적으로 동작한다")
    void matches_메서드가_정상적으로_동작한다() {
        // given
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when
        // 파라미터 순서 오류 - 순서가 바뀌면 검증 실패로 항상 false 반환
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // then
        assertTrue(matches);
    }
}
