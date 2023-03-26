package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.services.ClientService;

@RestController
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping("/{message}")
    public void get(@PathVariable String message) {
        clientService.get(message);
    }

}
