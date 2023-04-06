package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationReference;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.exception.CompilationNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final ReferenceRepository referenceRepository;
    private final EventRepository eventRepository;
    private final HitClient hitClient;
    static final String URI = "/events/";
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public CompilationDto post(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.
                save(CompilationMapper.mapNewCompilationDtoToCompilation(newCompilationDto));
        for (Long eventId: newCompilationDto.getEvents()) {
            CompilationReference reference = new CompilationReference();
            reference.setEventId(eventId);
            reference.setCompilationId(compilation.getId());
            referenceRepository.save(reference);
        }
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            compilationDto.setEvents(List.of());
            return compilationDto;
        }
        List<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        List<String> uris = new ArrayList<>();
        for (Event event: events) {
            uris.add(URI + event.getId());
        }
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.now().minusYears(10).format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), uris, false);
        Map<Long, Long> hits = new HashMap<>();
        for (ViewStatsDto viewStatsDto: stats) {
            Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
            hits.put(id, viewStatsDto.getHits());
        }
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event: events) {
            eventShortDtos.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId())));
        }
        compilationDto.setEvents(eventShortDtos);
        return compilationDto;
    }

    @Transactional
    public void delete(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found."));
        referenceRepository.deleteByCompilationId(compilation.getId());
        compilationRepository.deleteById(compilation.getId());
    }

    @Transactional
    public CompilationDto patch(UpdateCompilationRequest request, Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found."));
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            referenceRepository.deleteByCompilationId(compilation.getId());
            for (Long eventId: request.getEvents()) {
                CompilationReference reference = new CompilationReference();
                reference.setEventId(eventId);
                reference.setCompilationId(compilation.getId());
                referenceRepository.save(reference);
            }
        }
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
        List<Event> events = eventRepository.findByIdIn(request.getEvents());
        List<String> uris = new ArrayList<>();
        for (Event event: events) {
            uris.add(URI + event.getId());
        }
        List<ViewStatsDto> stats = hitClient.get(LocalDateTime.now().minusYears(10).format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), uris, false);
        Map<Long, Long> hits = new HashMap<>();
        for (ViewStatsDto viewStatsDto: stats) {
            Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
            hits.put(id, viewStatsDto.getHits());
        }
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event: events) {
            eventShortDtos.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId())));
        }
        compilationDto.setEvents(eventShortDtos);
        return compilationDto;
    }

    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, pageable);
        List<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilation compilation: compilations) {
            CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
            List<Long> eventIds = referenceRepository.findEventIdsByCompilationId(compilation.getId());
            if (!eventIds.isEmpty()) {
                List<Event> events = eventRepository.findByIdIn(eventIds);
                List<String> uris = new ArrayList<>();
                for (Event event : events) {
                    uris.add(URI + event.getId());
                }
                List<ViewStatsDto> stats = hitClient.get(LocalDateTime.now().minusYears(10).format(DATE_TIME_FORMATTER),
                        LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), uris, false);
                Map<Long, Long> hits = new HashMap<>();
                for (ViewStatsDto viewStatsDto : stats) {
                    Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
                    hits.put(id, viewStatsDto.getHits());
                }
                List<EventShortDto> eventShortDtos = new ArrayList<>();
                for (Event event : events) {
                    eventShortDtos.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId())));
                }
                compilationDto.setEvents(eventShortDtos);
            } else {
                compilationDto.setEvents(List.of());
            }
            compilationDtos.add(compilationDto);
        }
        return compilationDtos;
    }

    public CompilationDto get(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found."));
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
        List<Long> eventIds = referenceRepository.findEventIdsByCompilationId(compilation.getId());
        if (!eventIds.isEmpty()) {
            List<Event> events = eventRepository.findByIdIn(eventIds);
            List<String> uris = new ArrayList<>();
            for (Event event : events) {
                uris.add(URI + event.getId());
            }
            List<ViewStatsDto> stats = hitClient.get(LocalDateTime.now().minusYears(10).format(DATE_TIME_FORMATTER),
                    LocalDateTime.now().plusYears(10).format(DATE_TIME_FORMATTER), uris, false);
            Map<Long, Long> hits = new HashMap<>();
            for (ViewStatsDto viewStatsDto : stats) {
                Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
                hits.put(id, viewStatsDto.getHits());
            }
            List<EventShortDto> eventShortDtos = new ArrayList<>();
            for (Event event : events) {
                eventShortDtos.add(EventMapper.mapEventToEventShortDto(event, hits.get(event.getId())));
            }
            compilationDto.setEvents(eventShortDtos);
        } else {
            compilationDto.setEvents(List.of());
        }
        return compilationDto;
    }
}
