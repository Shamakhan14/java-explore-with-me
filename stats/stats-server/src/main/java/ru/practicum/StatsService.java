package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository repository;

    public void addHit(EndpointHitDto endpointHitDto) {
        repository.save(EndpointHitMapper.mapDtoToHit(endpointHitDto));
    }

    public List<ViewStatsDto> get(String start, String end, String[] uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime formattedStart = LocalDateTime.parse(start, formatter);
        LocalDateTime formattedEnd = LocalDateTime.parse(end, formatter);
        List<EndpointHit> hits = repository.getHits(uris, formattedStart, formattedEnd);
        Map<String, ViewStatsDto> result = new HashMap<>();
        if (unique.equals(false)) {
            for (EndpointHit hit: hits) {
                ViewStatsDto stat = result.getOrDefault(hit.getApp() + " " + hit.getUri(), null);
                if (stat == null) {
                    result.put(hit.getApp() + " " + hit.getUri(), new ViewStatsDto(hit.getApp(), hit.getUri(), 1));
                } else {
                    stat.setHits(stat.getHits() + 1);
                }
            }
        } else {
            Map<String, EndpointHit> sortedHits = new HashMap<>();
            for (EndpointHit hit: hits) {
                if (!sortedHits.containsKey(hit.getApp() + " " + hit.getUri() + " " + hit.getIp())) {
                    sortedHits.put(hit.getApp() + " " + hit.getUri() + " " + hit.getIp(), hit);
                    ViewStatsDto stat = result.getOrDefault(hit.getApp() + " " + hit.getUri(), null);
                    if (stat == null) {
                        result.put(hit.getApp() + " " + hit.getUri(), new ViewStatsDto(hit.getApp(), hit.getUri(), 1));
                    } else {
                        stat.setHits(stat.getHits() + 1);
                    }
                }
            }
        }
        return new ArrayList<>(result.values());
    }
}
