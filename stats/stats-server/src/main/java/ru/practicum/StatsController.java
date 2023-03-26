package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public List<ViewStatsDto> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        List<ViewStatsDto> response = service.get(start, end, uris, unique);
        log.info("Статистика собрана.");
        return response;
    }
}
