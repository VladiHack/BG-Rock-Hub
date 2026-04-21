package bg.sofia.bgrockHub.repository;

import bg.sofia.bgrockHub.entity.Event;
import bg.sofia.bgrockHub.entity.enums.EventStatus;
import bg.sofia.bgrockHub.entity.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    Page<Event> findByCity(String city, Pageable pageable);
    Page<Event> findByGenre(Genre genre, Pageable pageable);
    Page<Event> findByStatusAndCity(EventStatus status, String city, Pageable pageable);
    Page<Event> findByVenueId(Long venueId, Pageable pageable);

    @Query("SELECT e FROM Event e JOIN e.bands b WHERE b.id = :bandId ORDER BY e.eventDate DESC")
    Page<Event> findByBandId(@Param("bandId") Long bandId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Event> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.eventDate > :now AND e.status = 'UPCOMING' ORDER BY e.eventDate ASC")
    Page<Event> findUpcoming(@Param("now") LocalDateTime now, Pageable pageable);
}
