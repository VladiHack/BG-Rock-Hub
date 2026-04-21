package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.Follow;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.enums.Genre;
import bg.sofia.bgrockHub.entity.enums.Role;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.repository.BandRepository;
import bg.sofia.bgrockHub.repository.FollowRepository;
import bg.sofia.bgrockHub.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock private FollowRepository followRepository;
    @Mock private UserRepository userRepository;
    @Mock private BandRepository bandRepository;

    @InjectMocks private FollowService followService;

    private User fan;
    private Band band;

    @BeforeEach
    void setUp() {
        fan = User.builder().id(1L).email("fan@example.com")
                .username("fan").role(Role.FAN).build();

        band = Band.builder().id(10L).name("Контра")
                .genre(Genre.HARD_ROCK).owner(fan).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setCurrentUser(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    // ─── toggleFollow ─────────────────────────────────────────────────────────

    @Test
    void shouldReturnFollowingTrueWhenUserFollowsBandForFirstTime() {
        setCurrentUser(fan);
        when(userRepository.findByEmail("fan@example.com")).thenReturn(Optional.of(fan));
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(followRepository.findByFollowerIdAndBandId(1L, 10L)).thenReturn(Optional.empty());
        when(followRepository.save(any(Follow.class))).thenReturn(
                Follow.builder().follower(fan).band(band).build());
        when(followRepository.countByBandId(10L)).thenReturn(1L);

        Map<String, Object> result = followService.toggleFollow(10L);

        assertThat(result.get("following")).isEqualTo(true);
        assertThat(result.get("followersCount")).isEqualTo(1L);
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void shouldReturnFollowingFalseWhenUserUnfollowsBand() {
        setCurrentUser(fan);
        Follow existing = Follow.builder().follower(fan).band(band).build();

        when(userRepository.findByEmail("fan@example.com")).thenReturn(Optional.of(fan));
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(followRepository.findByFollowerIdAndBandId(1L, 10L)).thenReturn(Optional.of(existing));
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        Map<String, Object> result = followService.toggleFollow(10L);

        assertThat(result.get("following")).isEqualTo(false);
        assertThat(result.get("followersCount")).isEqualTo(0L);
        verify(followRepository).delete(existing);
    }

    @Test
    void shouldThrowNotFoundWhenToggleFollowForNonExistentBand() {
        setCurrentUser(fan);
        when(userRepository.findByEmail("fan@example.com")).thenReturn(Optional.of(fan));
        when(bandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.toggleFollow(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── isFollowing ──────────────────────────────────────────────────────────

    @Test
    void shouldReturnTrueWhenUserIsFollowingBand() {
        setCurrentUser(fan);
        when(userRepository.findByEmail("fan@example.com")).thenReturn(Optional.of(fan));
        when(followRepository.existsByFollowerIdAndBandId(1L, 10L)).thenReturn(true);

        assertThat(followService.isFollowing(10L)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserIsNotFollowingBand() {
        setCurrentUser(fan);
        when(userRepository.findByEmail("fan@example.com")).thenReturn(Optional.of(fan));
        when(followRepository.existsByFollowerIdAndBandId(1L, 10L)).thenReturn(false);

        assertThat(followService.isFollowing(10L)).isFalse();
    }
}
