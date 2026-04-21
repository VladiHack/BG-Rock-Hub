package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.review.ReviewRequest;
import bg.sofia.bgrockHub.dto.review.ReviewResponse;
import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.Event;
import bg.sofia.bgrockHub.entity.Review;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.Venue;
import bg.sofia.bgrockHub.entity.enums.EventStatus;
import bg.sofia.bgrockHub.entity.enums.Genre;
import bg.sofia.bgrockHub.entity.enums.ReviewTargetType;
import bg.sofia.bgrockHub.entity.enums.Role;
import bg.sofia.bgrockHub.exception.DuplicateResourceException;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
import bg.sofia.bgrockHub.repository.*;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private BandRepository bandRepository;
    @Mock private VenueRepository venueRepository;
    @Mock private EventRepository eventRepository;

    @InjectMocks private ReviewService reviewService;

    private User reviewer;
    private User adminUser;
    private User otherUser;
    private Band band;
    private Venue venue;
    private Event event;
    private Review review;

    @BeforeEach
    void setUp() {
        reviewer = User.builder().id(1L).email("reviewer@example.com")
                .username("reviewer").role(Role.FAN).build();
        adminUser = User.builder().id(2L).email("admin@example.com")
                .username("admin").role(Role.ADMIN).build();
        otherUser = User.builder().id(3L).email("other@example.com")
                .username("other").role(Role.FAN).build();

        band = Band.builder().id(10L).name("Контра").genre(Genre.HARD_ROCK)
                .owner(reviewer).build();
        venue = Venue.builder().id(20L).name("Mixtape 5").address("ул. Рила 2")
                .city("Sofia").owner(reviewer).build();
        event = Event.builder().id(30L).title("Rock Night").city("Sofia")
                .eventDate(LocalDateTime.now().plusDays(7))
                .status(EventStatus.UPCOMING).organizer(reviewer).build();

        review = Review.builder()
                .id(100L).rating(5).content("Страхотни!")
                .targetType(ReviewTargetType.BAND).targetId(10L)
                .reviewer(reviewer).isApproved(true)
                .createdAt(LocalDateTime.now())
                .build();
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

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void shouldReturnReviewResponseWhenCreateForBandSucceeds() {
        setCurrentUser(reviewer);
        ReviewRequest request = new ReviewRequest(5, "Страхотни!", ReviewTargetType.BAND, 10L);

        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(reviewer));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, ReviewTargetType.BAND, 10L))
                .thenReturn(false);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.calculateAverageRating(ReviewTargetType.BAND, 10L)).thenReturn(5.0);
        when(reviewRepository.countByTarget(ReviewTargetType.BAND, 10L)).thenReturn(1L);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));

        ReviewResponse response = reviewService.create(request);

        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.targetType()).isEqualTo(ReviewTargetType.BAND);
        assertThat(response.reviewerId()).isEqualTo(1L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldReturnReviewResponseWhenCreateForVenueSucceeds() {
        setCurrentUser(reviewer);
        ReviewRequest request = new ReviewRequest(4, "Добро място", ReviewTargetType.VENUE, 20L);
        Review venueReview = Review.builder().id(101L).rating(4).content("Добро място")
                .targetType(ReviewTargetType.VENUE).targetId(20L)
                .reviewer(reviewer).isApproved(true).createdAt(LocalDateTime.now()).build();

        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(reviewer));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, ReviewTargetType.VENUE, 20L))
                .thenReturn(false);
        when(venueRepository.findById(20L)).thenReturn(Optional.of(venue));
        when(reviewRepository.save(any(Review.class))).thenReturn(venueReview);
        when(reviewRepository.calculateAverageRating(ReviewTargetType.VENUE, 20L)).thenReturn(4.0);
        when(reviewRepository.countByTarget(ReviewTargetType.VENUE, 20L)).thenReturn(1L);
        when(venueRepository.findById(20L)).thenReturn(Optional.of(venue));

        ReviewResponse response = reviewService.create(request);

        assertThat(response.targetType()).isEqualTo(ReviewTargetType.VENUE);
        assertThat(response.rating()).isEqualTo(4);
    }

    @Test
    void shouldReturnReviewResponseWhenCreateForEventSucceeds() {
        setCurrentUser(reviewer);
        ReviewRequest request = new ReviewRequest(3, "Добро събитие", ReviewTargetType.EVENT, 30L);
        Review eventReview = Review.builder().id(102L).rating(3).content("Добро събитие")
                .targetType(ReviewTargetType.EVENT).targetId(30L)
                .reviewer(reviewer).isApproved(true).createdAt(LocalDateTime.now()).build();

        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(reviewer));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, ReviewTargetType.EVENT, 30L))
                .thenReturn(false);
        when(eventRepository.findById(30L)).thenReturn(Optional.of(event));
        when(reviewRepository.save(any(Review.class))).thenReturn(eventReview);
        when(reviewRepository.calculateAverageRating(ReviewTargetType.EVENT, 30L)).thenReturn(3.0);
        when(reviewRepository.countByTarget(ReviewTargetType.EVENT, 30L)).thenReturn(1L);
        when(eventRepository.findById(30L)).thenReturn(Optional.of(event));

        ReviewResponse response = reviewService.create(request);

        assertThat(response.targetType()).isEqualTo(ReviewTargetType.EVENT);
    }

    @Test
    void shouldThrowDuplicateExceptionWhenUserAlreadyReviewedTarget() {
        setCurrentUser(reviewer);
        ReviewRequest request = new ReviewRequest(5, "Дублирано", ReviewTargetType.BAND, 10L);

        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(reviewer));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, ReviewTargetType.BAND, 10L))
                .thenReturn(true);

        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Вече имате ревю");
    }

    @Test
    void shouldThrowNotFoundWhenCreateForNonExistentBand() {
        setCurrentUser(reviewer);
        ReviewRequest request = new ReviewRequest(5, "test", ReviewTargetType.BAND, 999L);

        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(reviewer));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, ReviewTargetType.BAND, 999L))
                .thenReturn(false);
        when(bandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getForTarget ─────────────────────────────────────────────────────────

    @Test
    void shouldReturnPageWhenGetForTargetSucceeds() {
        when(reviewRepository.findByTargetTypeAndTargetIdAndIsApproved(
                eq(ReviewTargetType.BAND), eq(10L), eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));

        PageResponse<ReviewResponse> page = reviewService.getForTarget(ReviewTargetType.BAND, 10L, 0, 10);

        assertThat(page.content()).hasSize(1);
        assertThat(page.content().get(0).rating()).isEqualTo(5);
    }

    @Test
    void shouldReturnEmptyPageWhenNoReviewsForTarget() {
        when(reviewRepository.findByTargetTypeAndTargetIdAndIsApproved(
                eq(ReviewTargetType.BAND), eq(10L), eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        PageResponse<ReviewResponse> page = reviewService.getForTarget(ReviewTargetType.BAND, 10L, 0, 10);

        assertThat(page.content()).isEmpty();
        assertThat(page.totalElements()).isZero();
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    void shouldReturnUpdatedReviewWhenOwnerUpdates() {
        setCurrentUser(reviewer);
        ReviewRequest updateReq = new ReviewRequest(4, "Обновено", ReviewTargetType.BAND, 10L);

        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(reviewer));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.calculateAverageRating(ReviewTargetType.BAND, 10L)).thenReturn(4.0);
        when(reviewRepository.countByTarget(ReviewTargetType.BAND, 10L)).thenReturn(1L);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));

        ReviewResponse response = reviewService.update(100L, updateReq);

        assertThat(response).isNotNull();
        verify(reviewRepository).save(review);
    }

    @Test
    void shouldThrowUnauthorizedWhenNonOwnerTriesToUpdateReview() {
        setCurrentUser(otherUser);
        ReviewRequest updateReq = new ReviewRequest(1, "Хак", ReviewTargetType.BAND, 10L);

        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> reviewService.update(100L, updateReq))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowNotFoundWhenUpdateWithMissingReview() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.update(999L,
                new ReviewRequest(5, "test", ReviewTargetType.BAND, 10L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    void shouldDeleteReviewWhenOwnerDeletes() {
        setCurrentUser(reviewer);
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(reviewer));
        when(reviewRepository.calculateAverageRating(ReviewTargetType.BAND, 10L)).thenReturn(null);
        when(reviewRepository.countByTarget(ReviewTargetType.BAND, 10L)).thenReturn(0L);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));

        reviewService.delete(100L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void shouldDeleteReviewWhenAdminDeletes() {
        setCurrentUser(adminUser);
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(reviewRepository.calculateAverageRating(ReviewTargetType.BAND, 10L)).thenReturn(null);
        when(reviewRepository.countByTarget(ReviewTargetType.BAND, 10L)).thenReturn(0L);
        when(bandRepository.findById(10L)).thenReturn(Optional.of(band));

        reviewService.delete(100L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void shouldThrowUnauthorizedWhenNonOwnerTriesToDeleteReview() {
        setCurrentUser(otherUser);
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> reviewService.delete(100L))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowNotFoundWhenDeleteWithMissingReview() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
