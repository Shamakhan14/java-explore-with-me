package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory_Id(Long categoryId);

    List<Event> findByInitiator_Id(Long userId, Pageable pageable);

    @Query(value = "select e from Event e " +
            "left join User u on e.initiator.id = u.id " +
            "where e.initiator.id in :users and " +
            "e.state in :states and " +
            "e.category.id in :categories and " +
            "e.eventDate between :start and :end " +
            "group by e.id ")
    List<Event> search(@Param("users") List<Long> userIds, @Param("states") List<State> states,
                       @Param("categories")List<Long> categories, @Param("start") LocalDateTime rangeStart,
                       @Param("end") LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategory_IdInAndPaidAndEventDateBetweenAndState(String text1,
        String text2, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
        State state, Pageable pageable);

    List<Event> findByIdIn(List<Long> ids);
}
