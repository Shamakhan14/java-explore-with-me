package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
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
        return new ApiError(StatusCode.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUniqueException(final ConstraintViolationException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFoundException(final UserNotFoundException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.NOT_FOUND,
                "The required object was not found.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFoundException(final CategoryNotFoundException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.NOT_FOUND,
                "The required object was not found.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNumberFormatException(final NumberFormatException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenCategoryDeleteException(final ForbiddenCategoryDeleteException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.CONFLICT,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleEventValidationException(final EventValidationException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.FORBIDDEN,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handlePatchingPublishedEventException(final PatchingPublishedEventException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.FORBIDDEN,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRequestOwnEventException(final RequestValidationException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.CONFLICT,
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleRequestNotFoundException(final RequestNotFoundException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.NOT_FOUND,
                "The required object was not found.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompilationNotFoundException(final CompilationNotFoundException exception) {
        log.info("{} : {}", exception.getClass().toString(), exception.getMessage());
        return new ApiError(StatusCode.NOT_FOUND,
                "The required object was not found.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }
}
