package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto post(@PathVariable Long userId, @RequestParam Long eventId) {
        ParticipationRequestDto requestDto = requestService.post(userId, eventId);
        log.info("Заявка успешно создана.");
        return requestDto;
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto patch(@PathVariable Long userId, @PathVariable Long requestId) {
        ParticipationRequestDto requestDto = requestService.patch(userId, requestId);
        log.info("Заявка успешно отменена.");
        return requestDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> get(@PathVariable Long userId) {
        List<ParticipationRequestDto> requestDtos = requestService.get(userId);
        log.info("Выведен список заявок пользователя.");
        return requestDtos;
    }
}
