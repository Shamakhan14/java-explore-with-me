package ru.practicum.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEventId(Long eventId);

    List<Comment> findByEventId(Long eventId, Pageable pageable);

    List<Comment> findByEventIdIn(List<Long> eventIds);

    @Query(value = "select c.eventId, count(c) from Comment c " +
            "where c.eventId in ?1 " +
            "group by c.eventId")
    Map<Long, Integer> findCommentAmoutForEvents(List<Long> eventIds);
}
