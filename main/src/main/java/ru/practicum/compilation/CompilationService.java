package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationReference;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.hit.HitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final ReferenceRepository referenceRepository;
    private final HitService hitService;

    @Transactional
    public CompilationDto post(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository
                .save(CompilationMapper.mapNewCompilationDtoToCompilation(newCompilationDto));
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            List<CompilationReference> references = new ArrayList<>();
            for (Long eventId : newCompilationDto.getEvents()) {
                CompilationReference reference = new CompilationReference();
                reference.setEventId(eventId);
                reference.setCompilationId(compilation.getId());
                references.add(reference);
            }
            referenceRepository.saveAll(references);
        }
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            compilationDto.setEvents(List.of());
            return compilationDto;
        }
        compilationDto.setEvents(hitService.giveEventShortDtosToCompilation(newCompilationDto.getEvents()));
        return compilationDto;
    }

    @Transactional
    public void delete(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation not found."));
        referenceRepository.deleteByCompilationId(compilation.getId());
        compilationRepository.deleteById(compilation.getId());
    }

    @Transactional
    public CompilationDto patch(UpdateCompilationRequest request, Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation not found."));
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            referenceRepository.deleteByCompilationId(compilation.getId());
            List<CompilationReference> references = new ArrayList<>();
            for (Long eventId: request.getEvents()) {
                CompilationReference reference = new CompilationReference();
                reference.setEventId(eventId);
                reference.setCompilationId(compilation.getId());
                references.add(reference);
            }
            referenceRepository.saveAll(references);
        }
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
        compilationDto.setEvents(hitService.giveEventShortDtosToCompilation(request.getEvents()));
        return compilationDto;
    }

    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }
        if (compilations.isEmpty()) {
            return List.of();
        }
        List<Long> compilationIds = compilations.stream()
                .map(Compilation::getId)
                .collect(Collectors.toList());
        List<CompilationReference> references = referenceRepository.findByCompilationIdIn(compilationIds);
        Map<Long, List<Long>> sortedEventIds = new HashMap<>();
        for (CompilationReference reference: references) {
            if (!sortedEventIds.containsKey(reference.getCompilationId())) {
                sortedEventIds.put(reference.getCompilationId(), new ArrayList<>());
            }
            sortedEventIds.get(reference.getCompilationId()).add(reference.getEventId());
        }
        List<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilation compilation: compilations) {
            CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
            if (sortedEventIds.containsKey(compilation.getId())) {
                compilationDto.setEvents(hitService
                        .giveEventShortDtosToCompilation(sortedEventIds.get(compilation.getId())));
            } else {
                compilationDto.setEvents(List.of());
            }
            compilationDtos.add(compilationDto);
        }
        return compilationDtos;
    }

    public CompilationDto get(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation not found."));
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
        List<Long> eventIds = referenceRepository.findEventIdsByCompilationId(compilation.getId());
        if (!eventIds.isEmpty()) {
            compilationDto.setEvents(hitService.giveEventShortDtosToCompilation(eventIds));
        } else {
            compilationDto.setEvents(List.of());
        }
        return compilationDto;
    }
}
