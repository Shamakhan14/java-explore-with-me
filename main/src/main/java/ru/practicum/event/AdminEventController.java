package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> search(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<State> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        List<EventFullDto> response = eventService.search(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Список событий выведен.");
        return response;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchAdmin(@PathVariable Long eventId, @RequestBody @Validated UpdateEventAdminRequest request) {
        EventFullDto response = eventService.updateAdmin(eventId, request);
        log.info("Информация о событии обновлена.");
        return response;
    }
}
