package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto post(@RequestBody @Validated NewCompilationDto newCompilationDto) {
        CompilationDto compilationDto = compilationService.post(newCompilationDto);
        log.info("Подборка событий успешно создана.");
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
        log.info("Подборка событий успешно удалена.");
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patch(@RequestBody @Valid UpdateCompilationRequest request, @PathVariable Long compId) {
        CompilationDto compilationDto = compilationService.patch(request, compId);
        log.info("Подборка событий успешно обновлена.");
        return compilationDto;
    }
}
