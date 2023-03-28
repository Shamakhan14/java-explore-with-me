package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository repository;

    @Transactional
    public void addHit(EndpointHitDto endpointHitDto) {
        repository.save(EndpointHitMapper.mapDtoToHit(endpointHitDto));
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                return repository.getStatsUnique(start, end, uris);
            }
            return repository.getStatsNotUnique(start, end, uris);
        } else {
            if (unique) {
                return repository.getStatsUniqueUrisNull(start, end);
            }
            return repository.getStatsNotUniqueUrisNull(start, end);
        }
    }
}
