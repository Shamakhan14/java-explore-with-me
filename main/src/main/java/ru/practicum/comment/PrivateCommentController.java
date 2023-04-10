package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto post(@PathVariable Long userId, @PathVariable Long eventId,
                           @RequestBody @Valid NewCommentDto newCommentDto) {
        CommentDto commentDto = commentService.post(userId, eventId, newCommentDto);
        log.info("Комментарий добавлен.");
        return commentDto;
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto patch(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commentId,
                            @RequestBody @Valid NewCommentDto newCommentDto) {
        CommentDto commentDto = commentService.patch(userId, eventId, commentId, newCommentDto);
        log.info("Комментарий изменен.");
        return commentDto;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commentId) {
        commentService.delete(userId, eventId, commentId);
        log.info("Комментарий удален.");
    }
}
