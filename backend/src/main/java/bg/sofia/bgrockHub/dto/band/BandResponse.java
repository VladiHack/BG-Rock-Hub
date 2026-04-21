package bg.sofia.bgrockHub.dto.band;

import bg.sofia.bgrockHub.entity.enums.Genre;
import java.time.LocalDateTime;
import java.util.List;

public record BandResponse(
        Long id,
        String name,
        Genre genre,
        String description,
        String city,
        Integer foundedYear,
        String avatarUrl,
        String spotifyUrl,
        String youtubeUrl,
        String facebookUrl,
        String instagramUrl,
        String members,
        boolean isVerified,
        Double avgRating,
        Integer totalRatings,
        Long ownerId,
        String ownerUsername,
        List<String> photos,
        long followersCount,
        LocalDateTime createdAt
) {}
