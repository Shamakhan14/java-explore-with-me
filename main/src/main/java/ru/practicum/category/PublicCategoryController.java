package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        List<CategoryDto> response = categoryService.getAll(from, size);
        log.info("Выведен список категорий.");
        return response;
    }

    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable Long catId) {
        CategoryDto response = categoryService.get(catId);
        log.info("Выведена информация о категории.");
        return response;
    }
}
