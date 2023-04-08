package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestState;

import java.util.List;
import java.util.Map;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByRequester(Long userId);

    List<ParticipationRequest> findByEvent(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> ids);

    List<ParticipationRequest> findByEventAndStatus(Long eventId, RequestState status);

    @Query(value = "select req.event, count(req) from ParticipationRequest req " +
            "where req.event in ?1 and " +
            "req.status = ?2 " +
            "group by req.event")
    Map<Long, Integer> findConfirmedRequestsForEvents(List<Long> eventIds, RequestState state);
}
