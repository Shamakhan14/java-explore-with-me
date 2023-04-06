package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory_Id(Long categoryId);

    List<Event> findByInitiator_Id(Long userId, Pageable pageable);

    @Query(value = "select e from Event e " +
            "where e.initiator.id in ?1 and " +
            "e.state in ?2 and " +
            "e.category.id in ?3 and " +
            "e.eventDate > ?4 and " +
            "e.eventDate < ?5 " +
            "group by e.id ")
    List<Event> search(List<Long> userIds, List<State> states, List<Long> categories,
                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query(value = "select e from Event e " +
            "where lower(e.annotation) like lower(concat('%', ?1, '%')) or " +
            "lower(e.description) like lower(concat('%', ?1, '%')) and " +
            "e.category.id in ?2 and " +
            "e.paid = ?3 and " +
            "e.eventDate between ?4 and ?5 and " +
            "e.state = ?6" +
            "group by e.id")
    List<Event> searchPublicAll(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, State state, Pageable pageable);

    @Query(value = "select e from Event e " +
            "where (lower(e.annotation) like lower(concat('%', ?1, '%')) or " +
            "lower(e.description) like lower(concat('%', ?1, '%'))) and " +
            "e.category.id in ?2 and " +
            "e.paid = ?3 and " +
            "e.eventDate between ?4 and ?5 and " +
            "e.state = ?6 and " +
            "(e.confirmedRequests < e.participantLimit or " +
            "e.participantLimit <> 0) " +
            "group by e.id")
    List<Event> searchPublicAvailable(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, State state, Pageable pageable);

    List<Event> findByIdIn(List<Long> ids);
}
