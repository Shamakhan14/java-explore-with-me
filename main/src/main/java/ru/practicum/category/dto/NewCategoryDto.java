package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCategoryDto {
    @NotBlank(message = "Field: name. Error: must not be blank.")
    @Size(max = 50, message = "Field: name. Error: must be less than 50 characters.")
    private String name;
}
