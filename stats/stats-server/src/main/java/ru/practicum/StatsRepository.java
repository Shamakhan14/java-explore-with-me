package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select h from EndpointHit h " +
            "where h.uri in ?1 and " +
            "h.timeStamp between ?2 and ?3 " +
            "group by h.id " +
            "order by h.timeStamp desc")
    List<EndpointHit> getHits(String[] uris, LocalDateTime start, LocalDateTime end);
}
