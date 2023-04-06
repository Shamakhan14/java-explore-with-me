package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewEventDto {
    @NotBlank(message = "Field: annotation. Error: must not be blank.")
    @Size(min = 20, max = 2000, message = "Field: annotation. Error: size must be between 20 and 2000 characters.")
    private String annotation;
    @NotNull(message = "Field: category. Error: must not be null.")
    private Long category;
    @NotBlank(message = "Field: description. Error: must not be blank.")
    @Size(min = 20, max = 7000, message = "Field: description. Error: size must be between 20 and 7000 characters.")
    private String description;
    @NotNull(message = "Field: eventDate. Error: must not be null.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull(message = "Field: location. Error: must not be null.")
    private Location location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotBlank(message = "Field: title. Error: must not be blank.")
    @Size(min = 3, max = 120, message = "Field: title. Error: size must be between 3 and 120 characters.")
    private String title;
}
