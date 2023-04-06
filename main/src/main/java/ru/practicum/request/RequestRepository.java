package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByRequester(Long userId);

    List<ParticipationRequest> findByEvent(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> ids);
}
