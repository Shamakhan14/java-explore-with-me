package ru.practicum;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HitClient {
    private final RestTemplate rest;
    private final String serverUrl;

    public HitClient(@Value("${stats-server.url}") String serverUrl, RestTemplate rest) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto);
        rest.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public List<ViewStatsDto> get(String start, String end, List<String> uris, Boolean unique) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        if (!uris.isEmpty()) {
            parameters.put("uris", uris);
        }
        parameters.put("unique", unique);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<Object> response = rest
                .exchange(serverUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        HttpMethod.GET, requestEntity, Object.class, parameters);
        List<ViewStatsDto> result = objectMapper.convertValue(response.getBody(), new TypeReference<>(){});
        if (result == null) {
            return List.of();
        } else {
            return result;
        }
    }
}
