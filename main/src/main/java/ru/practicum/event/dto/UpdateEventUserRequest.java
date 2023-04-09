package ru.practicum.event.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateEventUserRequest extends UpdateEventRequest {
    private StateAction stateAction;
}
