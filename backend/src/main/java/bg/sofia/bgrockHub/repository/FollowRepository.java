package bg.sofia.bgrockHub.repository;

import bg.sofia.bgrockHub.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndBandId(Long followerId, Long bandId);
    boolean existsByFollowerIdAndBandId(Long followerId, Long bandId);
    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);
    Page<Follow> findByBandId(Long bandId, Pageable pageable);
    long countByBandId(Long bandId);
}
