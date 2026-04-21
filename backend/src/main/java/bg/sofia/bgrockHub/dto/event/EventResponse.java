package bg.sofia.bgrockHub.dto.event;

import bg.sofia.bgrockHub.dto.band.BandResponse;
import bg.sofia.bgrockHub.dto.venue.VenueResponse;
import bg.sofia.bgrockHub.entity.enums.EventStatus;
import bg.sofia.bgrockHub.entity.enums.Genre;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime eventDate,
        String city,
        BigDecimal ticketPrice,
        String coverImgUrl,
        EventStatus status,
        Genre genre,
        Integer interestedCount,
        Double avgRating,
        Integer totalRatings,
        VenueResponse venue,
        Long organizerId,
        String organizerUsername,
        Set<BandResponse> bands,
        List<String> photos,
        LocalDateTime createdAt
) {}
