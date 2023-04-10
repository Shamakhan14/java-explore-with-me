package ru.practicum.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event mapNewEventDtoToEvent(NewEventDto newEventDto, Category category, User user) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiator(user);
        event.setLocationLat(newEventDto.getLocation().getLat());
        event.setLocationLon(newEventDto.getLocation().getLon());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setPublishedOn(null);
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setState(State.PENDING);
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static EventFullDto mapEventToEventFullDto(Event event, Long views, Integer confirmedRequests,
                                                      Integer commentDtos) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                confirmedRequests,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                UserMapper.mapUserToUserShortDto(event.getInitiator()),
                new Location(event.getLocationLat(), event.getLocationLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                views,
                commentDtos
        );
    }

    public static EventShortDto mapEventToEventShortDto(Event event, Long views, Integer confirmedRequests,
                                                        Integer comments) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                confirmedRequests,
                event.getEventDate(),
                event.getId(),
                UserMapper.mapUserToUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views,
                comments
        );
    }
}
