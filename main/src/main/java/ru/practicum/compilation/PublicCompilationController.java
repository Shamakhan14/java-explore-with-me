package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        List<CompilationDto> response = compilationService.getAll(pinned, from, size);
        log.info("Подборки событий выведены.");
        return response;
    }

    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable Long compId) {
        CompilationDto response = compilationService.get(compId);
        log.info("Подборка событий выведена.");
        return response;
    }
}
