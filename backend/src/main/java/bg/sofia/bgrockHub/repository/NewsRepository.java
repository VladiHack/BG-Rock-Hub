package bg.sofia.bgrockHub.repository;

import bg.sofia.bgrockHub.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByIsPublishedOrderByCreatedAtDesc(boolean isPublished, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.isPublished = true AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<News> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT n FROM News n JOIN n.tags t WHERE t = :tag AND n.isPublished = true")
    Page<News> findByTag(@Param("tag") String tag, Pageable pageable);
}
