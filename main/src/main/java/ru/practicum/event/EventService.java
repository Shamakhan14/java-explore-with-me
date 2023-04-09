package ru.practicum.event;

import org.springframework.beans.factory.annotation.Value;
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
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.*;
import ru.practicum.hit.HitService;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.RequestState;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional(readOnly = true)
public class EventService {
    private final HitService hitService;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final HitClient hitClient;
    private final String app;
    static final String URI = "/events/";
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository,
                        UserRepository userRepository, RequestRepository requestRepository, HitClient hitClient,
                        CommentRepository commentRepository, HitService hitService,
                        @Value("${app}") String app) {
        this.hitService = hitService;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.commentRepository = commentRepository;
        this.hitClient = hitClient;
        this.app = app;
    }

    @Transactional
    public EventFullDto post(Long userId, NewEventDto newEventDto) {
        LocalDateTime limit = LocalDateTime.now().plusHours(2);
        if (newEventDto.getEventDate().isBefore(limit)) {
            throw new DateConstraintException("Field: eventDate. Error: incorrect event date.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + userId + " was not found."));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Field: category. Error: category not found."));
        Event event = eventRepository.save(EventMapper.mapNewEventDtoToEvent(newEventDto, category, user));
        return EventMapper.mapEventToEventFullDto(event, 0L, 0, List.of());
    }

    public List<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Event> events = eventRepository.findByInitiator_Id(userId, pageable);
        if (events.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> hits = hitService.getHits(events);
        List<EventShortDto> result = new ArrayList<>();
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Integer> requests = requestRepository.findConfirmedRequestsForEvents(eventIds,
                RequestState.CONFIRMED);
        for (Event event: events) {
            result.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId()), requests.get(event.getId())));
        }
        return result;
    }

    public EventFullDto get(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        List<ParticipationRequest> requests = requestRepository.findByEventAndStatus(event.getId(),
                RequestState.CONFIRMED);
        List<Comment> comments = commentRepository.findByEventId(eventId);
        return EventMapper.mapEventToEventFullDto(event, hitService.getHit(event), requests.size(),
                CommentMapper.mapCommentsToCommentDtos(comments));
    }

    @Transactional
    public EventFullDto patch(UpdateEventUserRequest request, Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new PatchingPublishedEventException("Only pending or canceled events can be changed.");
        }
        LocalDateTime limit = LocalDateTime.now().plusHours(2);
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(limit)) {
                throw new DateConstraintException("Field: eventDate. Error: incorrect event date.");
            }
            event.setEventDate(request.getEventDate());
        }
        if (request.getAnnotation() != null && !request.getAnnotation().isBlank()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Field: category. Error: category not found."));
            event.setCategory(category);
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            if (request.getLocation().getLat() != null) {
                event.setLocationLat(request.getLocation().getLat());
            }
            if (request.getLocation().getLon() != null) {
                event.setLocationLon(request.getLocation().getLon());
            }
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
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        List<ParticipationRequest> requests = requestRepository.findByEventAndStatus(event.getId(),
                RequestState.CONFIRMED);
        List<Comment> comments = commentRepository.findByEventId(eventId);
        return EventMapper.mapEventToEventFullDto(event, hitService.getHit(event), requests.size(),
                CommentMapper.mapCommentsToCommentDtos(comments));
    }

    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        List<ParticipationRequest> requests = requestRepository.findByEvent(eventId);
        if (requests.isEmpty()) {
            return List.of();
        }
        return RequestMapper.mapRequestsToRequestDtos(requests);
    }

    @Transactional
    public EventRequestStatusUpdateResult patchRequests(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest requestUpdate) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        List<ParticipationRequest> confirmedRequests = requestRepository.findByEventAndStatus(eventId,
                RequestState.CONFIRMED);
        Integer confRequests = confirmedRequests.size();
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        if (requestUpdate.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult();
        }
        List<ParticipationRequest> requests = requestRepository.findByIdIn(requestUpdate.getRequestIds());
        if (event.getRequestModeration().equals(false) || event.getParticipantLimit() == 0L) {
            return new EventRequestStatusUpdateResult();
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        List<ParticipationRequestDto> approvedRequests = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            if (!requests.get(i).getStatus().equals(RequestState.PENDING)) {
                throw new RequestValidationException("Can't change status of canceled or published requests.");
            }
            if (requestUpdate.getStatus().equals(EventRequestStatus.REJECTED)) {
                requests.get(i).setStatus(RequestState.REJECTED);
                rejectedRequests.add(RequestMapper.mapRequestToRequestDto(requests.get(i)));
            } else {
                if (event.getParticipantLimit() != 0 &&
                        confRequests.equals(event.getParticipantLimit())) {
                    throw new RequestValidationException("Can't approve request. Participation limit reached.");
                }
                requests.get(i).setStatus(RequestState.CONFIRMED);
                approvedRequests.add(RequestMapper.mapRequestToRequestDto(requests.get(i)));
                confRequests++;
                if (confRequests.equals(event.getParticipantLimit()) && i != requests.size() - 1) {
                    for (int j = i + 1; j < requests.size(); j++) {
                        requests.get(j).setStatus(RequestState.REJECTED);
                        rejectedRequests.add(RequestMapper.mapRequestToRequestDto(requests.get(j)));
                    }
                    break;
                }
            }
        }
        result.setRejectedRequests(rejectedRequests);
        result.setConfirmedRequests(approvedRequests);
        return result;
    }

    public List<EventFullDto> search(List<Long> users, List<State> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (users == null) {
            users = new ArrayList<>();
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().plusYears(10);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(10);
        }
        if (states == null) {
            states = new ArrayList<>();
        }
        if (categories == null) {
            categories = new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Event> events = eventRepository.search(users, states, categories, rangeStart, rangeEnd, pageable);
        if (events.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> hits = hitService.getHits(events);
        List<Long> eventIds = new ArrayList<>();
        for (Event event: events) {
            eventIds.add(event.getId());
        }
        Map<Long, Integer> confRequests = requestRepository
                .findConfirmedRequestsForEvents(eventIds,RequestState.CONFIRMED);
        List<EventFullDto> result = new ArrayList<>();
        List<Comment> comments = commentRepository.findByEventIdIn(eventIds);
        Map<Long, List<CommentDto>> commentDtos = new HashMap<>();
        for (Comment comment: comments) {
            if (!commentDtos.containsKey(comment.getEventId())) {
                commentDtos.put(comment.getEventId(), new ArrayList<>());
            }
            commentDtos.get(comment.getEventId()).add(CommentMapper.mapCommentToCommentDto(comment));
        }
        for (Event event: events) {
            result.add(EventMapper.mapEventToEventFullDto(event, hits.get(event.getId()),
                    confRequests.get(event.getId()), commentDtos.get(event.getId())));
        }
        return result;
    }

    @Transactional
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        LocalDateTime limit = LocalDateTime.now().plusHours(1);
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(limit)) {
                throw new DateConstraintException("Field: eventDate. Error: incorrect event date.");
            }
            event.setEventDate(request.getEventDate());
        }
        if (request.getAnnotation() != null && !request.getAnnotation().isBlank()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Field: category. Error: category not found."));
            event.setCategory(category);
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            if (request.getLocation().getLat() != null) {
                event.setLocationLat(request.getLocation().getLat());
            }
            if (request.getLocation().getLon() != null) {
                event.setLocationLon(request.getLocation().getLon());
            }
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
            if (request.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(State.PENDING)) {
                    throw new EventPublishingException("Can't publish published or cancelled event.");
                }
                event.setState(State.PUBLISHED);
            } else {
                if (event.getState().equals(State.PUBLISHED)) {
                    throw new EventPublishingException("Can't cancel published event.");
                }
                event.setState(State.CANCELED);
            }
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        List<ParticipationRequest> requests = requestRepository.findByEventAndStatus(event.getId(),
                RequestState.CONFIRMED);
        List<Comment> comments = commentRepository.findByEventId(eventId);
        return EventMapper.mapEventToEventFullDto(event, hitService.getHit(event), requests.size(),
                CommentMapper.mapCommentsToCommentDtos(comments));
    }

    public List<EventShortDto> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, SortEvent sort,
                                            Integer from, Integer size, String ip) {
        if (text == null || text.isBlank()) {
            text = "";
        } else {
            text = text.toLowerCase();
        }
        if (categories == null || categories.isEmpty()) {
            categories = List.of();
        }
        if (rangeEnd == null && rangeStart == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.now().plusYears(10);
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(10);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(10);
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Event> events;
        events = eventRepository.searchAll(text, categories, paid, rangeStart, rangeEnd, State.PUBLISHED, pageable);
        if (onlyAvailable.equals(true)) {
            List<Event> eventsToRemove = new ArrayList<>();
            List<Long> eventIds = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
            Map<Long, Integer> requests = requestRepository.findConfirmedRequestsForEvents(eventIds,
                    RequestState.CONFIRMED);
            for (Event event: events) {
                if (event.getParticipantLimit() != 0 && requests.get(event.getId()).equals(event.getParticipantLimit())) {
                    eventsToRemove.add(event);
                }
            }
            events.removeAll(eventsToRemove);
        }
        EndpointHitDto endpointHitDto = new EndpointHitDto(app, "/events", ip, LocalDateTime.now());
        hitClient.addHit(endpointHitDto);
        if (events.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> hits = hitService.getHits(events);
        List<Long> eventIds = new ArrayList<>();
        for (Event event: events) {
            eventIds.add(event.getId());
        }
        Map<Long, Integer> confRequests = requestRepository
                .findConfirmedRequestsForEvents(eventIds,RequestState.CONFIRMED);
        List<EventShortDto> result = new ArrayList<>();
        for (Event event: events) {
            result.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId()),
                    confRequests.get(event.getId())));
        }
        if (sort != null) {
            if (sort.equals(SortEvent.EVENT_DATE)) {
                Collections.sort(result, Comparator.comparing(EventShortDto::getEventDate));
            } else {
                Collections.sort(result, Comparator.comparing(EventShortDto::getViews));
            }
        }
        return result;
    }

    public EventFullDto getPublic(Long eventId, String ip) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        String fullUri = URI + eventId;
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.now().minusYears(10).format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), List.of(fullUri), false);
        EndpointHitDto endpointHitDto = new EndpointHitDto(app, URI + event.getId(), ip, LocalDateTime.now());
        hitClient.addHit(endpointHitDto);
        List<ParticipationRequest> requests = requestRepository.findByEventAndStatus(event.getId(),
                RequestState.CONFIRMED);
        List<CommentDto> comments = CommentMapper.mapCommentsToCommentDtos(commentRepository.findByEventId(eventId));
        if (stats.isEmpty()) {
            return EventMapper.mapEventToEventFullDto(event, 0L, requests.size(), comments);
        } else {
            return EventMapper.mapEventToEventFullDto(event, stats.get(0).getHits(), requests.size(), comments);
        }
    }

    @Transactional
    public CommentDto postComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + userId + " was not found."));
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        Comment comment = commentRepository.save(CommentMapper.mapNewCommentDtoToComment(newCommentDto, user, eventId));
        return CommentMapper.mapCommentToCommentDto(comment);
    }
}
