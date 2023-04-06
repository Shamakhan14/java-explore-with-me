package ru.practicum.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.model.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "event_id")
    private Long event;
    @Column(name = "requester")
    private Long requester;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private State status;
}
