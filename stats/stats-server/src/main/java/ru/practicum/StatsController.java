package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHitDto endpointHitDto) {
        service.addHit(endpointHitDto);
        log.info("Информация сохранена.");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) String[] uris,
                                  @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        if (uris == null || uris.length == 0) return Collections.emptyList();
        List<ViewStatsDto> response = service.get(start, end, uris, unique);
        log.info("Статистика собрана.");
        return response;
    }
}
