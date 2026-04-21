package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.band.BandRequest;
import bg.sofia.bgrockHub.dto.band.BandResponse;
import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.enums.Genre;
import bg.sofia.bgrockHub.entity.enums.Role;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
import bg.sofia.bgrockHub.repository.BandRepository;
import bg.sofia.bgrockHub.repository.FollowRepository;
import bg.sofia.bgrockHub.repository.ReviewRepository;
import bg.sofia.bgrockHub.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BandServiceTest {

    @Mock private BandRepository bandRepository;
    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private BandService bandService;

    private User owner;
    private User otherUser;
    private User adminUser;
    private Band band;
    private BandRequest bandRequest;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).email("owner@example.com")
                .username("owner").role(Role.BAND).build();
        otherUser = User.builder().id(2L).email("other@example.com")
                .username("other").role(Role.FAN).build();
        adminUser = User.builder().id(3L).email("admin@example.com")
                .username("admin").role(Role.ADMIN).build();

        band = Band.builder()
                .id(10L).name("Контра").genre(Genre.HARD_ROCK)
                .description("Легендарна банда").city("Sofia")
                .foundedYear(1987).owner(owner)
                .avgRating(4.7).totalRatings(120)
                .build();

        bandRequest = new BandRequest("Контра", Genre.HARD_ROCK,
                "Легендарна банда", "Sofia", 1987,
                null, null, null, null, null, "Петър, Иван, Георги");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private void setCurrentUser(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void shouldReturnBandResponseWhenCreateSucceeds() {
        setCurrentUser(owner);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(bandRepository.save(any(Band.class))).thenReturn(band);
        when(followRepository.countByBandId(10L)).thenReturn(42L);

        BandResponse response = bandService.create(bandRequest);

        assertThat(response.name()).isEqualTo("Контра");
        assertThat(response.genre()).isEqualTo(Genre.HARD_ROCK);
        assertThat(response.followersCount()).isEqualTo(42L);
        verify(bandRepository).save(any(Band.class));
    }

    @Test
    void shouldThrowNotFoundWhenCreateCalledByUnknownUser() {
        setCurrentUser(owner);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bandService.create(bandRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getById ──────────────────────────────────────────────────────────────

    @Test
    void shouldReturnBandResponseWhenGetByIdSucceeds() {
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(followRepository.countByBandId(10L)).thenReturn(5L);

        BandResponse response = bandService.getById(10L);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Контра");
        assertThat(response.city()).isEqualTo("Sofia");
    }

    @Test
    void shouldThrowNotFoundWhenGetByIdWithMissingBand() {
        when(bandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bandService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAll ───────────────────────────────────────────────────────────────

    @Test
    void shouldReturnPageWhenGetAllWithNoFilters() {
        when(bandRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(band)));
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        PageResponse<BandResponse> page = bandService.getAll(0, 12, null, null, null);

        assertThat(page.content()).hasSize(1);
        assertThat(page.totalElements()).isEqualTo(1);
    }

    @Test
    void shouldReturnPageWhenGetAllWithSearchQuery() {
        when(bandRepository.search(eq("Контра"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(band)));
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        PageResponse<BandResponse> page = bandService.getAll(0, 12, null, null, "Контра");

        assertThat(page.content()).hasSize(1);
        verify(bandRepository).search(eq("Контра"), any(Pageable.class));
    }

    @Test
    void shouldReturnPageWhenGetAllFilteredByGenre() {
        when(bandRepository.findByGenre(eq(Genre.HARD_ROCK), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(band)));
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        PageResponse<BandResponse> page = bandService.getAll(0, 12, Genre.HARD_ROCK, null, null);

        assertThat(page.content()).hasSize(1);
        verify(bandRepository).findByGenre(eq(Genre.HARD_ROCK), any(Pageable.class));
    }

    @Test
    void shouldReturnPageWhenGetAllFilteredByCity() {
        when(bandRepository.findByCity(eq("Sofia"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(band)));
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        PageResponse<BandResponse> page = bandService.getAll(0, 12, null, "Sofia", null);

        assertThat(page.content()).hasSize(1);
        verify(bandRepository).findByCity(eq("Sofia"), any(Pageable.class));
    }

    @Test
    void shouldReturnPageWhenGetAllFilteredByGenreAndCity() {
        when(bandRepository.findByGenreAndCity(eq(Genre.HARD_ROCK), eq("Sofia"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(band)));
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        PageResponse<BandResponse> page = bandService.getAll(0, 12, Genre.HARD_ROCK, "Sofia", null);

        assertThat(page.content()).hasSize(1);
        verify(bandRepository).findByGenreAndCity(eq(Genre.HARD_ROCK), eq("Sofia"), any(Pageable.class));
    }

    // ─── getTopRated ──────────────────────────────────────────────────────────

    @Test
    void shouldReturnListWhenGetTopRatedSucceeds() {
        when(bandRepository.findTopRated(any(Pageable.class))).thenReturn(List.of(band));
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        List<BandResponse> result = bandService.getTopRated(6);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).avgRating()).isEqualTo(4.7);
    }

    // ─── getUnknownBands ──────────────────────────────────────────────────────

    @Test
    void shouldReturnListWhenGetUnknownBandsSucceeds() {
        Band unknown = Band.builder().id(20L).name("Unknown").genre(Genre.INDIE_ROCK)
                .city("Plovdiv").owner(owner).avgRating(0.0).totalRatings(0).build();
        when(bandRepository.findUnknownBands(any(Pageable.class))).thenReturn(List.of(unknown));
        when(followRepository.countByBandId(20L)).thenReturn(0L);

        List<BandResponse> result = bandService.getUnknownBands(20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Unknown");
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    void shouldReturnUpdatedBandWhenOwnerUpdates() {
        setCurrentUser(owner);
        BandRequest updateReq = new BandRequest("Контра v2", Genre.METAL,
                "Обновено", "Plovdiv", 1990,
                null, null, null, null, null, "Петър");

        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(bandRepository.save(any(Band.class))).thenReturn(band);
        when(followRepository.countByBandId(10L)).thenReturn(0L);

        BandResponse response = bandService.update(10L, updateReq);

        assertThat(response).isNotNull();
        verify(bandRepository).save(band);
    }

    @Test
    void shouldThrowUnauthorizedWhenNonOwnerTriesToUpdate() {
        setCurrentUser(otherUser);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> bandService.update(10L, bandRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowNotFoundWhenUpdateWithMissingBand() {
        when(bandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bandService.update(99L, bandRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    void shouldDeleteBandWhenOwnerDeletes() {
        setCurrentUser(owner);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

        bandService.delete(10L);

        verify(bandRepository).delete(band);
    }

    @Test
    void shouldDeleteBandWhenAdminDeletes() {
        setCurrentUser(adminUser);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        bandService.delete(10L);

        verify(bandRepository).delete(band);
    }

    @Test
    void shouldThrowUnauthorizedWhenNonOwnerTriesToDelete() {
        setCurrentUser(otherUser);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> bandService.delete(10L))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowNotFoundWhenDeleteWithMissingBand() {
        when(bandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bandService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
