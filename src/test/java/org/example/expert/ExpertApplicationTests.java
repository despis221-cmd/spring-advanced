package org.example.expert;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExpertApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_메서드가_정상적으로_실행된다() {
        ExpertApplication.main(new String[]{});
    }

}
