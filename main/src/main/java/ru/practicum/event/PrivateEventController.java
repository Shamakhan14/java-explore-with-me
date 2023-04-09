package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto post(@PathVariable Long userId,
                             @RequestBody @Validated NewEventDto newEventDto) {
        EventFullDto eventFullDto = eventService.post(userId, newEventDto);
        log.info("Событие успешно создано.");
        return eventFullDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        List<EventShortDto> eventFullDto = eventService.getAll(userId, from, size);
        log.info("Выведена информация о событиях пользователя.");
        return eventFullDto;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto get(@PathVariable Long userId, @PathVariable Long eventId) {
        EventFullDto eventFullDto = eventService.get(userId, eventId);
        log.info("Выведена информация о событии пользователя.");
        return eventFullDto;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patch(@RequestBody @Validated UpdateEventUserRequest request,
                              @PathVariable Long userId,
                              @PathVariable Long eventId) {
        EventFullDto eventFullDto = eventService.patch(request, userId, eventId);
        log.info("Информация о событии пользователя изменена.");
        return eventFullDto;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        List<ParticipationRequestDto> requestDtos = eventService.getRequests(userId, eventId);
        log.info("Выведена информация о запросах события.");
        return requestDtos;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchRequests(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest request) {
        EventRequestStatusUpdateResult requestDtos = eventService.patchRequests(userId, eventId, request);
        log.info("Информация о запросах обновлена.");
        return requestDtos;
    }
}
