package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class HitClient {
    private final RestTemplate rest;
    @Value("${client.url}")
    private String serverUrl;
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addHit(EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto);
        rest.exchange(serverUrl + "hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(DATE_TIME_FORMATTER));
        parameters.put("end", end.format(DATE_TIME_FORMATTER));
        if (!uris.isEmpty()) {
            parameters.put("uris", uris);
        }
        parameters.put("unique", unique);
        HttpEntity<String> requestEntity = new HttpEntity<>("");
        ResponseEntity<ViewStatsDto[]> response = rest
                .exchange(serverUrl + "stats", HttpMethod.GET, requestEntity, ViewStatsDto[].class, parameters);
        ViewStatsDto[] result = response.getBody();
        if (result == null) {
            return List.of();
        } else {
            return Arrays.asList(result);
        }
    }
}
