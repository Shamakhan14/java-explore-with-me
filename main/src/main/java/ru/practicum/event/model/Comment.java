package ru.practicum.event.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment_text")
    private String text;
    @Column(name = "event_id")
    private Long eventId;
    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;
    @Column(name = "created")
    private LocalDateTime created;
}
