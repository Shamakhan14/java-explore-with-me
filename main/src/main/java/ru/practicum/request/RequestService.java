package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestState;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ParticipationRequestDto post(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        List<ParticipationRequest> requests = requestRepository.findByEventAndStatus(eventId, RequestState.CONFIRMED);
        if (event.getInitiator().getId().equals(userId)) {
            throw new RequestValidationException("Initiator can't request participation in their own event.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new RequestValidationException("Event has to be published.");
        }
        if (event.getParticipantLimit() != 0 && requests.size() == event.getParticipantLimit()) {
            throw new RequestValidationException("Participants limit has already been reached.");
        }
        ParticipationRequest newRequest = new ParticipationRequest();
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setEvent(eventId);
        newRequest.setRequester(userId);
        if (event.getRequestModeration().equals(false)) {
            newRequest.setStatus(RequestState.CONFIRMED);
        } else {
            newRequest.setStatus(RequestState.PENDING);
        }
        ParticipationRequest request = requestRepository.save(newRequest);
        return RequestMapper.mapRequestToRequestDto(request);
    }

    @Transactional
    public ParticipationRequestDto patch(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id = " + requestId + " was not found."));
        if (!request.getRequester().equals(userId)) {
            throw new EntityNotFoundException("Request with id = " + requestId + " was not found.");
        }
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.mapRequestToRequestDto(request);
    }

    public List<ParticipationRequestDto> get(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        List<ParticipationRequest> requests = requestRepository.findByRequester(userId);
        if (requests.isEmpty()) {
            return List.of();
        } else {
            return RequestMapper.mapRequestsToRequestDtos(requests);
        }
    }
}
