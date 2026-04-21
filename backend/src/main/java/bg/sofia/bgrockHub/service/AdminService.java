package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.admin.AdminStatsResponse;
import bg.sofia.bgrockHub.dto.admin.AdminUserResponse;
import bg.sofia.bgrockHub.dto.band.BandResponse;
import bg.sofia.bgrockHub.dto.review.ReviewResponse;
import bg.sofia.bgrockHub.dto.venue.VenueResponse;
import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.Venue;
import bg.sofia.bgrockHub.entity.enums.Role;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BandRepository bandRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final ReviewRepository reviewRepository;
    private final FollowRepository followRepository;

    // ─── Stats ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        return new AdminStatsResponse(
                userRepository.count(),
                bandRepository.count(),
                venueRepository.count(),
                eventRepository.count(),
                reviewRepository.count(),
                reviewRepository.countByIsApproved(false)
        );
    }

    // ─── Users ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> getUsers(int page, int size) {
        Page<User> users = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return new PageResponse<>(
                users.getContent().stream().map(this::toUserResponse).toList(),
                users.getNumber(), users.getSize(),
                users.getTotalElements(), users.getTotalPages(), users.isLast()
        );
    }

    @Transactional
    public AdminUserResponse changeUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Потребител", userId));
        user.setRole(role);
        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public AdminUserResponse toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Потребител", userId));
        user.setActive(!user.isActive());
        return toUserResponse(userRepository.save(user));
    }

    // ─── Bands ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<BandResponse> getBands(int page, int size) {
        Page<Band> bands = bandRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return new PageResponse<>(
                bands.getContent().stream().map(this::toBandResponse).toList(),
                bands.getNumber(), bands.getSize(),
                bands.getTotalElements(), bands.getTotalPages(), bands.isLast()
        );
    }

    @Transactional
    @CacheEvict(value = {"bands", "topBands"}, allEntries = true)
    public BandResponse verifyBand(Long bandId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Банда", bandId));
        band.setVerified(!band.isVerified());
        return toBandResponse(bandRepository.save(band));
    }

    @Transactional
    @CacheEvict(value = {"bands", "topBands"}, allEntries = true)
    public void deleteBand(Long bandId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Банда", bandId));
        bandRepository.delete(band);
    }

    // ─── Venues ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<VenueResponse> getVenues(int page, int size) {
        Page<Venue> venues = venueRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return new PageResponse<>(
                venues.getContent().stream().map(this::toVenueResponse).toList(),
                venues.getNumber(), venues.getSize(),
                venues.getTotalElements(), venues.getTotalPages(), venues.isLast()
        );
    }

    @Transactional
    @CacheEvict(value = "venues", allEntries = true)
    public VenueResponse verifyVenue(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Клуб", venueId));
        venue.setVerified(!venue.isVerified());
        return toVenueResponse(venueRepository.save(venue));
    }

    // ─── Reviews ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getReviews(int page, int size, Boolean approved) {
        Page<bg.sofia.bgrockHub.entity.Review> reviews;
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (approved != null) {
            reviews = reviewRepository.findByIsApproved(approved, pageable);
        } else {
            reviews = reviewRepository.findAll(pageable);
        }

        return new PageResponse<>(
                reviews.getContent().stream().map(this::toReviewResponse).toList(),
                reviews.getNumber(), reviews.getSize(),
                reviews.getTotalElements(), reviews.getTotalPages(), reviews.isLast()
        );
    }

    @Transactional
    public ReviewResponse approveReview(Long reviewId) {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Ревю", reviewId));
        review.setApproved(!review.isApproved());
        return toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Ревю", reviewId));
        reviewRepository.delete(review);
    }

    // ─── Mappers ─────────────────────────────────────────────────────────────

    private AdminUserResponse toUserResponse(User u) {
        return new AdminUserResponse(u.getId(), u.getEmail(), u.getUsername(),
                u.getRole(), u.getCity(), u.isVerified(), u.isActive(), u.getCreatedAt());
    }

    private BandResponse toBandResponse(Band b) {
        long followers = followRepository.countByBandId(b.getId());
        return new BandResponse(b.getId(), b.getName(), b.getGenre(), b.getDescription(),
                b.getCity(), b.getFoundedYear(), b.getAvatarUrl(),
                b.getSpotifyUrl(), b.getYoutubeUrl(), b.getFacebookUrl(), b.getInstagramUrl(),
                b.getMembers(), b.isVerified(), b.getAvgRating(), b.getTotalRatings(),
                b.getOwner().getId(), b.getOwner().getUsername(),
                b.getPhotos(), followers, b.getCreatedAt());
    }

    private VenueResponse toVenueResponse(Venue v) {
        return new VenueResponse(v.getId(), v.getName(), v.getAddress(), v.getCity(),
                v.getDescription(), v.getCapacity(), v.getPhone(), v.getWebsite(),
                v.getCoverImgUrl(), v.isVerified(), v.getAvgRating(), v.getTotalRatings(),
                v.getOwner().getId(), v.getOwner().getUsername(),
                v.getPhotos(), v.getCreatedAt());
    }

    private ReviewResponse toReviewResponse(bg.sofia.bgrockHub.entity.Review r) {
        return new ReviewResponse(r.getId(), r.getRating(), r.getContent(),
                r.getTargetType(), r.getTargetId(),
                r.getReviewer().getId(), r.getReviewer().getUsername(), r.getReviewer().getAvatarUrl(),
                r.isApproved(), r.getCreatedAt());
    }
}
