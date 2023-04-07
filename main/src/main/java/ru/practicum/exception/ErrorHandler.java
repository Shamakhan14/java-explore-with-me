package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(final ConstraintViolationException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND,
                "The required object was not found.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNumberFormatException(final NumberFormatException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenCategoryDeleteException(final ForbiddenCategoryDeleteException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleEventValidationException(final EventValidationException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.FORBIDDEN,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlePatchingPublishedEventException(final PatchingPublishedEventException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRequestOwnEventException(final RequestValidationException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDateConstraintException(final DateConstraintException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventPublishingException(final EventPublishingException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }
}
