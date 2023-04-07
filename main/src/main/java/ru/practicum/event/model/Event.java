package ru.practicum.event.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.Category;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description")
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator")
    private User initiator;
    @Column(name = "location_lat")
    private float locationLat;
    @Column(name = "location_lon")
    private float locationLon;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
    @Column(name = "title")
    private String title;
}
