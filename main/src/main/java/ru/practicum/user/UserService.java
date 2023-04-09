package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto create(NewUserRequest userRequest) {
        User user = userRepository.save(UserMapper.mapUserRequestToUser(userRequest));
        return UserMapper.mapUserToUserDto(user);
    }

    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " was not found.");
        }
        userRepository.deleteById(userId);
    }

    public List<UserDto> get(List<Long> ids, Integer from, Integer size) {
        if (ids == null) {
            ids = List.of();
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<User> users = userRepository.findByIdIn(ids, pageable);
        return UserMapper.mapUsersToUserDtos(users);
    }
}
