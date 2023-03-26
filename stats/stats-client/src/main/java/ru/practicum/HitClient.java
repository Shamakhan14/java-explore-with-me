package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HitClient {
    private final RestTemplate rest = new RestTemplate();
    @Value("${client.url}")
    private String serverUrl;

    public void addHit(EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto);
        rest.exchange(serverUrl + "hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        parameters.put("start", start.format(dateTimeFormatter));
        parameters.put("end", end.format(dateTimeFormatter));
        if (!uris.isEmpty()) {
            parameters.put("uris", uris);
        }
        parameters.put("unique", unique);
        HttpEntity<ViewStatsDto[]> requestEntity = new HttpEntity<>(null);
        ResponseEntity<ViewStatsDto[]> response = rest
                .exchange(serverUrl + "stats", HttpMethod.GET, requestEntity, ViewStatsDto[].class, parameters);
        return Arrays.asList(response.getBody());
    }
}
