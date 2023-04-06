package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.exception.ForbiddenCategoryDeleteException;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto post(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(CategoryMapper.mapNewCategoryDtoToCategory(newCategoryDto));
        return CategoryMapper.mapCategoryToCategoryDto(category);
    }

    @Transactional
    public CategoryDto patch(CategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id = " + catId + " was not found."));
        category.setName(categoryDto.getName());
        return CategoryMapper.mapCategoryToCategoryDto(category);
    }

    @Transactional
    public void delete(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id = " + catId + " was not found."));
        List<Event> events = eventRepository.findByCategory_Id(catId);
        if (events.isEmpty()) {
            throw new ForbiddenCategoryDeleteException("The category is not empty.");
        }
        categoryRepository.deleteById(catId);
    }

    public List<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        return CategoryMapper.mapCategoriesToCategoryDtos(categories);
    }

    public CategoryDto get(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id = " + catId + " was not found."));
        return CategoryMapper.mapCategoryToCategoryDto(category);
    }
}
