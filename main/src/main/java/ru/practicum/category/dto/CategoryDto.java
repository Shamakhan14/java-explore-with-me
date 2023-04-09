package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryDto {
    private Long id;
    @NotBlank(groups = {Patch.class}, message = "Field: name. Error: must not be blank.")
    @Size(groups = {Patch.class}, max = 50, message = "Field: name. Error: must be less than 50 characters.")
    private String name;
}
