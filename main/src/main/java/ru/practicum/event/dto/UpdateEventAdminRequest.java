package ru.practicum.event.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateEventAdminRequest extends UpdateEventRequest {
    private AdminStateAction stateAction;
}
