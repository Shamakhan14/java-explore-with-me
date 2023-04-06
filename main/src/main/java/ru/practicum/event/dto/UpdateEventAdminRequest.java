package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "Field: annotation. Error: size must be between 20 and 2000 characters.")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "Field: description. Error: size must be between 20 and 7000 characters.")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    @Size(min = 3, max = 120, message = "Field: title. Error: size must be between 3 and 120 characters.")
    private String title;
}
