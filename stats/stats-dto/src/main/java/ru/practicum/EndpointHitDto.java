package ru.practicum;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EndpointHitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
