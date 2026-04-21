package bg.sofia.bgrockHub.dto.event;

import bg.sofia.bgrockHub.entity.enums.Genre;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventRequest(
        @NotBlank String title,
        String description,
        @NotNull @Future LocalDateTime eventDate,
        @NotBlank String city,
        BigDecimal ticketPrice,
        String coverImgUrl,
        Genre genre,
        Long venueId,
        List<Long> bandIds
) {}
