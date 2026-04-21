package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.Follow;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.repository.BandRepository;
import bg.sofia.bgrockHub.repository.FollowRepository;
import bg.sofia.bgrockHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final BandRepository bandRepository;

    @Transactional
    public Map<String, Object> toggleFollow(Long bandId) {
        User user = getCurrentUser();
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Банда", bandId));

        Optional<Follow> existing = followRepository.findByFollowerIdAndBandId(user.getId(), bandId);
        boolean following;
        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            following = false;
        } else {
            followRepository.save(Follow.builder().follower(user).band(band).build());
            following = true;
        }
        long count = followRepository.countByBandId(bandId);
        return Map.of("following", following, "followersCount", count);
    }

    public boolean isFollowing(Long bandId) {
        User user = getCurrentUser();
        return followRepository.existsByFollowerIdAndBandId(user.getId(), bandId);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен"));
    }
}
