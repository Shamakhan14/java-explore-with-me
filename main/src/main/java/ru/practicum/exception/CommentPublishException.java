package ru.practicum.exception;

public class CommentPublishException extends RuntimeException {

    public CommentPublishException(String message) {
        super(message);
    }
}
