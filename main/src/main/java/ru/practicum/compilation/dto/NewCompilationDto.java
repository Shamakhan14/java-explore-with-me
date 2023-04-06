package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned = false;
    @NotBlank(message = "Field: title. Error: must not be blank.")
    private String title;
}
