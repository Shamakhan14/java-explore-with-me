package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.SortEvent;
import ru.practicum.exception.DateConstraintException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllPublic(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(defaultValue = "false") Boolean paid,
                                            @RequestParam(required = false) LocalDateTime rangeStart,
                                            @RequestParam(required = false) LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(defaultValue = "EVENT_DATE") SortEvent sort,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10") @Positive Integer size,
                                            HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new DateConstraintException("Range start can't be after range end.");
        }
        String ip = request.getRemoteAddr();
        List<EventShortDto> response = eventService.getAllPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, ip);
        log.info("Выведена информация о событиях.");
        return response;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getPublic(@PathVariable Long eventId, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        EventFullDto response = eventService.getPublic(eventId, ip);
        log.info("Выведена информация о событии.");
        return response;
    }
}
