package ru.practicum.exception;

public class EventPublishingException extends RuntimeException {

    public EventPublishingException(String message) {
        super(message);
    }
}
