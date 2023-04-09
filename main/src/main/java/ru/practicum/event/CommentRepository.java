package ru.practicum.event;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEventId(Long eventId);

    List<Comment> findByEventIdIn(List<Long> eventIds);
}
