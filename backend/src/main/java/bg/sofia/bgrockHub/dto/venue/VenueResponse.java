package bg.sofia.bgrockHub.dto.venue;

import java.time.LocalDateTime;
import java.util.List;

public record VenueResponse(
        Long id,
        String name,
        String address,
        String city,
        String description,
        Integer capacity,
        String phone,
        String website,
        String coverImgUrl,
        boolean isVerified,
        Double avgRating,
        Integer totalRatings,
        Long ownerId,
        String ownerUsername,
        List<String> photos,
        LocalDateTime createdAt
) {}
