package bg.sofia.bgrockHub.repository;

import bg.sofia.bgrockHub.entity.Review;
import bg.sofia.bgrockHub.entity.enums.ReviewTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByTargetTypeAndTargetIdAndIsApproved(
            ReviewTargetType targetType, Long targetId, boolean isApproved, Pageable pageable);

    Optional<Review> findByReviewerIdAndTargetTypeAndTargetId(
            Long reviewerId, ReviewTargetType targetType, Long targetId);

    boolean existsByReviewerIdAndTargetTypeAndTargetId(
            Long reviewerId, ReviewTargetType targetType, Long targetId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetType = :type AND r.targetId = :id AND r.isApproved = true")
    Double calculateAverageRating(@Param("type") ReviewTargetType type, @Param("id") Long id);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.targetType = :type AND r.targetId = :id AND r.isApproved = true")
    Long countByTarget(@Param("type") ReviewTargetType type, @Param("id") Long id);

    Page<Review> findByIsApproved(boolean isApproved, Pageable pageable);

    long countByIsApproved(boolean isApproved);
}
