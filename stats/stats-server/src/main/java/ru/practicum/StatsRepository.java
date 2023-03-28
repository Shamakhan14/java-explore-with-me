package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE (h.timeStamp BETWEEN ?1 AND ?2) AND " +
            "(h.uri IN ?3) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(h.ip) DESC")
    List<ViewStatsDto> getStatsNotUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE (h.timeStamp BETWEEN ?1 AND ?2) AND " +
            "(h.uri IN ?3) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(DISTINCT h.ip) DESC")
    List<ViewStatsDto> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE (h.timeStamp BETWEEN ?1 AND ?2) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(h.ip) DESC")
    List<ViewStatsDto> getStatsUniqueUrisNull(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE (h.timeStamp BETWEEN ?1 AND ?2) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(DISTINCT h.ip) DESC")
    List<ViewStatsDto> getStatsNotUniqueUrisNull(LocalDateTime start, LocalDateTime end);
}
