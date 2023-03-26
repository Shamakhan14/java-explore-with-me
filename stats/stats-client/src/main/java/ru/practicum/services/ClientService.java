package ru.practicum.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientService {
    @Autowired
    private RestTemplate restTemplate;

    public void get(String message) {
        ResponseEntity<Object> forEntity = restTemplate.getForEntity("http://localhost:9090/" + message, Object.class);
    }
}
