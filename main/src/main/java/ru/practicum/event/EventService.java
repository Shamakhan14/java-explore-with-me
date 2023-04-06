package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.HitClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.*;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final HitClient hitClient;
    static final String URI = "/events/";
    static final String APP = "main-event-service";
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public EventFullDto post(Long userId, NewEventDto newEventDto) {
        LocalDateTime limit = LocalDateTime.now().plusHours(2);
        if (newEventDto.getEventDate().isBefore(limit)) {
            throw new EventValidationException("Field: eventDate. Error: incorrect event date.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " was not found."));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException("Field: category. Error: category not found."));
        Event event = eventRepository.save(EventMapper.mapNewEventDtoToEvent(newEventDto, category, user));
        return EventMapper.mapEventToEventFullDto(event, 0L);
    }

    public List<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " was not found."));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Event> events = eventRepository.findByInitiator_Id(userId, pageable);
        if (events.isEmpty()) {
            return List.of();
        }
        List<String> uris = new ArrayList<>();
        for (Event event: events) {
            uris.add(URI + event.getId().toString());
        }
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), uris, false);
        Map<Long, Long> hits = new HashMap<>();
        for (ViewStatsDto viewStatsDto: stats) {
            Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
            hits.put(id, viewStatsDto.getHits());
        }
        List<EventShortDto> result = new ArrayList<>();
        for (Event event: events) {
            result.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId())));
        }
        return result;
    }

    public EventFullDto get(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " was not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException("Event with id = " + eventId + " was not found");
        }
        String fullUri = URI + eventId;
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.MAX.format(DATE_TIME_FORMATTER), List.of(fullUri), false);
        if (stats.isEmpty()) {
            return EventMapper.mapEventToEventFullDto(event, 0L);
        } else {
            return EventMapper.mapEventToEventFullDto(event, stats.get(0).getHits());
        }
    }

    @Transactional
    public EventFullDto patch(UpdateEventUserRequest request, Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " was not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException("Event with id = " + eventId + " was not found");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new PatchingPublishedEventException("Only pending or canceled events can be changed.");
        }
        LocalDateTime limit = LocalDateTime.now().plusHours(2);
        if (request.getEventDate() != null) {
            if (request.getEventDate().isAfter(limit)) {
                throw new EventValidationException("Field: eventDate. Error: incorrect event date.");
            }
            event.setEventDate(request.getEventDate());
        }
        if (!request.getAnnotation().isBlank()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("Field: category. Error: category not found."));
            event.setCategory(category);
        }
        if (!request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            event.setLocationLat(request.getLocation().getLat());
            event.setLocationLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
        }
        if (!request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        String fullUri = URI + eventId;
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.MAX.format(DATE_TIME_FORMATTER), List.of(fullUri), false);
        if (stats.isEmpty()) {
            return EventMapper.mapEventToEventFullDto(event, 0L);
        } else {
            return EventMapper.mapEventToEventFullDto(event, stats.get(0).getHits());
        }
    }

    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " was not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException("Event with id = " + eventId + " was not found");
        }
        List<ParticipationRequest> requests = requestRepository.findByEvent(eventId);
        if (requests.isEmpty()) {
            return List.of();
        }
        return RequestMapper.mapRequestsToRequestDtos(requests);
    }

    @Transactional
    public List<ParticipationRequestDto> patchRequests(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest requestUpdate) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " was not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException("Event with id = " + eventId + " was not found");
        }
        if (requestUpdate.getRequestIds().isEmpty()) {
            return List.of();
        }
        List<ParticipationRequest> requests = requestRepository.findByIdIn(requestUpdate.getRequestIds());
        if (event.getRequestModeration().equals(false)) {
            return RequestMapper.mapRequestsToRequestDtos(requests);
        }
        for (int i = 0; i < requests.size(); i++) {
            if (!requests.get(i).getStatus().equals(State.PENDING)) {
                throw new RequestValidationException("Can't change status of canceled or published requests.");
            }
            if (requestUpdate.getStatus().equals(EventRequestStatus.REJECTED)) {
                requests.get(i).setStatus(State.CANCELED);
            } else {
                if (event.getParticipantLimit() != 0 &&
                        event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                    throw new RequestValidationException("Can't approve request. Participation limit reached.");
                }
                requests.get(i).setStatus(State.PUBLISHED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                if (event.getConfirmedRequests().equals(event.getParticipantLimit()) && i != requests.size() - 1) {
                    for (int j = i + 1; j < requests.size(); j++) {
                        requests.get(j).setStatus(State.CANCELED);
                    }
                    break;
                }
            }
        }
        return RequestMapper.mapRequestsToRequestDtos(requests);
    }

    public List<EventFullDto> search(List<Long> userIds, List<State> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.MIN;
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }
        if (states == null) {
            states = new ArrayList<>();
        }
        if (categories == null) {
            categories = new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Event> events = eventRepository.search(userIds, states, categories, rangeStart, rangeEnd, pageable);
        if (events.isEmpty()) {
            return List.of();
        }
        List<String> uris = new ArrayList<>();
        for (Event event: events) {
            uris.add(URI + event.getId());
        }
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.MAX.format(DATE_TIME_FORMATTER), uris, false);
        Map<Long, Long> hits = new HashMap<>();
        for (ViewStatsDto viewStatsDto: stats) {
            Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
            hits.put(id, viewStatsDto.getHits());
        }
        List<EventFullDto> result = new ArrayList<>();
        for (Event event: events) {
            result.add(EventMapper.mapEventToEventFullDto(event, hits.get(event.getId())));
        }
        return result;
    }

    @Transactional
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id = " + eventId + " was not found"));
        LocalDateTime limit = LocalDateTime.now().plusHours(1);
        if (request.getEventDate() != null) {
            if (request.getEventDate().isAfter(limit)) {
                throw new EventValidationException("Field: eventDate. Error: incorrect event date.");
            }
            event.setEventDate(request.getEventDate());
        }
        if (!request.getAnnotation().isBlank()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("Field: category. Error: category not found."));
            event.setCategory(category);
        }
        if (!request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            event.setLocationLat(request.getLocation().getLat());
            event.setLocationLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                if (!event.getState().equals(State.PENDING)) {
                    throw new EventValidationException("Can't publish published or cancelled event.");
                }
                event.setState(State.PUBLISHED);
            } else {
                if (!event.getState().equals(State.PUBLISHED)) {
                    throw new EventValidationException("Can't cancel published event.");
                }
                event.setState(State.CANCELED);
            }
        }
        if (!request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        String fullUri = URI + eventId;
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.MAX.format(DATE_TIME_FORMATTER), List.of(fullUri), false);
        if (stats.isEmpty()) {
            return EventMapper.mapEventToEventFullDto(event, 0L);
        } else {
            return EventMapper.mapEventToEventFullDto(event, stats.get(0).getHits());
        }
    }

    public List<EventShortDto> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, SortEvent sort,
                                            Integer from, Integer size, String ip) {
        if (rangeEnd == null && rangeStart == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.MAX;
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.MIN;
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Event> events;
        if (onlyAvailable.equals(false)) {
            events = eventRepository.searchPublicAll(text, categories, paid, rangeStart, rangeEnd, State.PUBLISHED,
                    pageable);
        } else {
            events = eventRepository.searchPublicAvailable(text, categories, paid, rangeStart, rangeEnd,
                    State.PUBLISHED, pageable);
        }
        if (events.isEmpty()) {
            return List.of();
        }
        List<String> uris = new ArrayList<>();
        for (Event event: events) {
            uris.add(URI + event.getId());
        }
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.MAX.format(DATE_TIME_FORMATTER), uris, false);
        Map<Long, Long> hits = new HashMap<>();
        for (ViewStatsDto viewStatsDto: stats) {
            Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
            hits.put(id, viewStatsDto.getHits());
        }
        List<EventShortDto> result = new ArrayList<>();
        for (Event event: events) {
            result.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId())));
        }
        if (sort.equals(SortEvent.EVENT_DATE)) {
            Collections.sort(result, Comparator.comparing(EventShortDto::getEventDate));
        } else {
            Collections.sort(result, Comparator.comparing(EventShortDto::getViews));
        }
        for (Event event: events) {
            EndpointHitDto endpointHitDto = new EndpointHitDto(APP, URI + event.getId(), ip, LocalDateTime.now());
            hitClient.addHit(endpointHitDto);
        }
        return result;
    }

    public EventShortDto getPublic(Long eventId, String ip) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventNotFoundException("Event with id = " + eventId + " was not found");
        }
        String fullUri = URI + eventId;
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.MAX.format(DATE_TIME_FORMATTER), List.of(fullUri), false);
        if (stats.isEmpty()) {
            return EventMapper.mapEventToEventShortDto(event, 0L);
        } else {
            return EventMapper.mapEventToEventShortDto(event, stats.get(0).getHits());
        }
    }
}
