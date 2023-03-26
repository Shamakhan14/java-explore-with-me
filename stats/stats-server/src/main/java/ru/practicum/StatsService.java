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

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        if (uris != null) {
            if (unique) {
                return repository.getStatsUnique(start, end, Arrays.asList(uris));
            }
            return repository.getStatsNotUnique(start, end, Arrays.asList(uris));
        } else {
            if (unique) {
                return repository.getStatsUniqueUrisNull(start, end);
            }
            return repository.getStatsNotUniqueUrisNull(start, end);
        }
    }
}
