package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    @NotNull
    private EventRequestStatus status;
}
