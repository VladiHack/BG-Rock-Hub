package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.review.ReviewRequest;
import bg.sofia.bgrockHub.dto.review.ReviewResponse;
import bg.sofia.bgrockHub.entity.Review;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.enums.ReviewTargetType;
import bg.sofia.bgrockHub.exception.DuplicateResourceException;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
import bg.sofia.bgrockHub.repository.BandRepository;
import bg.sofia.bgrockHub.repository.EventRepository;
import bg.sofia.bgrockHub.repository.ReviewRepository;
import bg.sofia.bgrockHub.repository.UserRepository;
import bg.sofia.bgrockHub.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BandRepository bandRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        User reviewer = getCurrentUser();

        if (reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(
                reviewer.getId(), request.targetType(), request.targetId())) {
            throw new DuplicateResourceException("Вече имате ревю за този обект");
        }

        validateTarget(request.targetType(), request.targetId());

        Review review = Review.builder()
                .rating(request.rating())
                .content(request.content())
                .targetType(request.targetType())
                .targetId(request.targetId())
                .reviewer(reviewer)
                .build();

        review = reviewRepository.save(review);
        updateTargetRating(request.targetType(), request.targetId());
        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getForTarget(ReviewTargetType type, Long targetId, int page, int size) {
        Page<Review> reviews = reviewRepository.findByTargetTypeAndTargetIdAndIsApproved(
                type, targetId, true, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return new PageResponse<>(reviews.getContent().stream().map(this::toResponse).toList(),
                reviews.getNumber(), reviews.getSize(), reviews.getTotalElements(),
                reviews.getTotalPages(), reviews.isLast());
    }

    @Transactional
    public ReviewResponse update(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ревю", id));
        User current = getCurrentUser();
        if (!review.getReviewer().getId().equals(current.getId())) {
            throw new UnauthorizedException("Можете да редактирате само собствените си ревюта");
        }
        review.setRating(request.rating());
        review.setContent(request.content());
        review = reviewRepository.save(review);
        updateTargetRating(review.getTargetType(), review.getTargetId());
        return toResponse(review);
    }

    @Transactional
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ревю", id));
        User current = getCurrentUser();
        boolean isAdmin = current.getRole().name().equals("ADMIN");
        if (!review.getReviewer().getId().equals(current.getId()) && !isAdmin) {
            throw new UnauthorizedException("Нямате права да изтриете това ревю");
        }
        ReviewTargetType type = review.getTargetType();
        Long targetId = review.getTargetId();
        reviewRepository.delete(review);
        updateTargetRating(type, targetId);
    }

    private void validateTarget(ReviewTargetType type, Long targetId) {
        switch (type) {
            case BAND -> bandRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Банда", targetId));
            case VENUE -> venueRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Клуб", targetId));
            case EVENT -> eventRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Събитие", targetId));
        }
    }

    private void updateTargetRating(ReviewTargetType type, Long targetId) {
        Double avg = reviewRepository.calculateAverageRating(type, targetId);
        Long count = reviewRepository.countByTarget(type, targetId);
        double rating = avg != null ? avg : 0.0;
        int total = count != null ? count.intValue() : 0;

        switch (type) {
            case BAND -> bandRepository.findById(targetId).ifPresent(b -> {
                b.setAvgRating(rating);
                b.setTotalRatings(total);
                bandRepository.save(b);
            });
            case VENUE -> venueRepository.findById(targetId).ifPresent(v -> {
                v.setAvgRating(rating);
                v.setTotalRatings(total);
                venueRepository.save(v);
            });
            case EVENT -> eventRepository.findById(targetId).ifPresent(e -> {
                e.setAvgRating(rating);
                e.setTotalRatings(total);
                eventRepository.save(e);
            });
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен"));
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(r.getId(), r.getRating(), r.getContent(),
                r.getTargetType(), r.getTargetId(),
                r.getReviewer().getId(), r.getReviewer().getUsername(), r.getReviewer().getAvatarUrl(),
                r.isApproved(), r.getCreatedAt());
    }
}
