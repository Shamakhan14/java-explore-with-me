package ru.practicum.exception;

public class ForbiddenCategoryDeleteException extends RuntimeException {

    public ForbiddenCategoryDeleteException(String message) {
        super(message);
    }
}
