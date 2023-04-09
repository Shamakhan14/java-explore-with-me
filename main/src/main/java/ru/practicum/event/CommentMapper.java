package ru.practicum.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.dto.NewCommentDto;
import ru.practicum.event.model.Comment;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment mapNewCommentDtoToComment(NewCommentDto newCommentDto, User user, Long eventId) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEventId(eventId);
        comment.setCreator(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto mapCommentToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getCreator().getName(), comment.getCreated());
    }

    public static List<CommentDto> mapCommentsToCommentDtos(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment: comments) {
            commentDtos.add(mapCommentToCommentDto(comment));
        }
        return commentDtos;
    }
}
