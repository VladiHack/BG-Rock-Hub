package bg.sofia.bgrockHub.repository;

import bg.sofia.bgrockHub.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findByCity(String city, Pageable pageable);

    @Query("SELECT v FROM Venue v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Venue> search(@Param("query") String query, Pageable pageable);

    Page<Venue> findByIsVerified(boolean isVerified, Pageable pageable);
}
