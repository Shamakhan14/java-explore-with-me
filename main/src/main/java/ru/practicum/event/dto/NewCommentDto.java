package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCommentDto {
    @NotBlank(message = "Field: text. Error: must not be blank.")
    @Size(max = 500, message = "Field: text. Error: size must be less than 500 characters.")
    private String text;
}
