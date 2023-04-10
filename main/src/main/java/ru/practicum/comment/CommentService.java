package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.CommentPublishException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentDto post(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + userId + " was not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new CommentPublishException("The event hasn't been published yet.");
        }
        Comment comment = commentRepository.save(CommentMapper.mapNewCommentDtoToComment(newCommentDto, user, eventId));
        return CommentMapper.mapCommentToCommentDto(comment);
    }

    @Transactional
    public CommentDto patch(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = " + commentId + " was not found."));
        if (!userId.equals(comment.getCreator().getId())) {
            throw new EntityNotFoundException("Comment with id = " + commentId + " was not found.");
        }
        comment.setText(newCommentDto.getText());
        comment.setChanged(LocalDateTime.now());
        return CommentMapper.mapCommentToCommentDto(comment);
    }

    @Transactional
    public void delete(Long userId, Long eventId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " was not found");
        }
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment with id = " + commentId + " was not found.");
        }
        commentRepository.deleteById(commentId);
    }

    public CommentDto get(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = " + commentId + " was not found."));
        return CommentMapper.mapCommentToCommentDto(comment);
    }

    public List<CommentDto> getAll(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new CommentPublishException("The event hasn't been published yet.");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Comment> comments = commentRepository.findByEventId(eventId, pageable);
        return CommentMapper.mapCommentsToCommentDtos(comments);
    }
}
