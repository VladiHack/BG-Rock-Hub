package bg.sofia.bgrockHub.dto.review;

import bg.sofia.bgrockHub.entity.enums.ReviewTargetType;
import jakarta.validation.constraints.*;

public record ReviewRequest(
        @NotNull @Min(1) @Max(5) Integer rating,
        @Size(max = 2000) String content,
        @NotNull ReviewTargetType targetType,
        @NotNull Long targetId
) {}
