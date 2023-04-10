package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.comment.CommentRepository;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final HitClient hitClient;
    static final String URI = "/events/";
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<EventShortDto> giveEventShortDtosToCompilation(List<Long> eventIds) {
        List<Event> events = eventRepository.findByIdIn(eventIds);
        Map<Long, Long> hits = getHits(events);
        Map<Long, Integer> comments = commentRepository.findCommentAmoutForEvents(eventIds);
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event: events) {
            List<ParticipationRequest> requests = requestRepository.findByEvent(event.getId());
            eventShortDtos.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId()), requests.size(),
                    comments.getOrDefault(event.getId(), 0)));
        }
        return eventShortDtos;
    }

    public Map<Long, Long> getHits(List<Event> events) {
        List<String> uris = new ArrayList<>();
        for (Event event: events) {
            uris.add(URI + event.getId().toString());
        }
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.now().minusYears(10).format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), uris, false);
        Map<Long, Long> hits = new HashMap<>();
        for (ViewStatsDto viewStatsDto: stats) {
            Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
            hits.put(id, viewStatsDto.getHits());
        }
        return hits;
    }

    public Long getHit(Event event) {
        String fullUri = URI + event.getId();
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.now().minusYears(10).format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), List.of(fullUri), false);
        if (stats.isEmpty()) {
            return 0L;
        } else {
            return stats.get(0).getHits();
        }
    }
}
