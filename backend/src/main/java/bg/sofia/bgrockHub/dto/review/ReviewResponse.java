package bg.sofia.bgrockHub.dto.review;

import bg.sofia.bgrockHub.entity.enums.ReviewTargetType;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Integer rating,
        String content,
        ReviewTargetType targetType,
        Long targetId,
        Long reviewerId,
        String reviewerUsername,
        String reviewerAvatarUrl,
        boolean isApproved,
        LocalDateTime createdAt
) {}
