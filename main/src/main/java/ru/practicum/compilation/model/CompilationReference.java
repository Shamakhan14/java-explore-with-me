package ru.practicum.compilation.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "compilation_references", schema = "public")
@Getter
@Setter
public class CompilationReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "compilation_id")
    private Long compilationId;
}
