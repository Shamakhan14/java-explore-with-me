package ru.practicum.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ParticipationRequestDto mapRequestToRequestDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getCreated(),
                request.getEvent(),
                request.getId(),
                request.getRequester(),
                request.getStatus());
    }

    public static List<ParticipationRequestDto> mapRequestsToRequestDtos(List<ParticipationRequest> requests) {
        List<ParticipationRequestDto> result = new ArrayList<>();
        for (ParticipationRequest request: requests) {
            result.add(mapRequestToRequestDto(request));
        }
        return result;
    }
}
