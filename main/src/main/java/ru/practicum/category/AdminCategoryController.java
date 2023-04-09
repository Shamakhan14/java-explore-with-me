package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.Patch;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto post(@RequestBody @Validated NewCategoryDto newCategoryDto) {
        CategoryDto categoryDto = categoryService.post(newCategoryDto);
        log.info("Категория успешно создана.");
        return categoryDto;
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patch(@RequestBody @Validated(Patch.class) CategoryDto categoryDto,
                             @PathVariable Long catId) {
        CategoryDto response = categoryService.patch(categoryDto, catId);
        log.info("Категория с ID {} успешно обновлена.", catId);
        return response;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
        log.info("Категория с ID {} успешно удалена.", catId);
    }
}
