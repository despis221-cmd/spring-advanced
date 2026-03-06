package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // JPQL fetch join → @EntityGraph로 변경
    @EntityGraph(attributePaths = {"user"})
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    // // 단건 조회도 동일하게 @EntityGraph 적용
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT t FROM Todo t WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    int countById(Long todoId);
}
