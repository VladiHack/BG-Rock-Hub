package bg.sofia.bgrockHub.repository;

import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BandRepository extends JpaRepository<Band, Long> {
    Page<Band> findByGenre(Genre genre, Pageable pageable);
    Page<Band> findByCity(String city, Pageable pageable);
    Page<Band> findByGenreAndCity(Genre genre, String city, Pageable pageable);

    @Query("SELECT b FROM Band b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Band> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT b FROM Band b ORDER BY b.avgRating DESC, b.totalRatings DESC")
    List<Band> findTopRated(Pageable pageable);

    @Query("SELECT b FROM Band b WHERE b.totalRatings < 10 ORDER BY b.createdAt DESC")
    List<Band> findUnknownBands(Pageable pageable);

    long countByOwnerId(Long ownerId);
}
