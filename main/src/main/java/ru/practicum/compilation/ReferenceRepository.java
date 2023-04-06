package ru.practicum.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilation.model.CompilationReference;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<CompilationReference, Long> {

    void deleteByCompilationId(Long compilationId);

    @Query(value = "select c.eventId from CompilationReference c " +
            "where c.compilationId = ?1 " +
            "group by c.eventId")
    List<Long> findEventIdsByCompilationId(Long compilationId);
}
