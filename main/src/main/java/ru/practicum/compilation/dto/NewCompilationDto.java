package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCompilationDto {
    private List<Long> events;
    private boolean pinned = false;
    @NotBlank(message = "Field: title. Error: must not be blank.")
    @Size(max = 200, message = "Field: title. Error: must be less than 200 characters.")
    private String title;
}
