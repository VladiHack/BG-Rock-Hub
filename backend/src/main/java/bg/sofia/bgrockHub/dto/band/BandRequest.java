package bg.sofia.bgrockHub.dto.band;

import bg.sofia.bgrockHub.entity.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BandRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull Genre genre,
        @Size(max = 2000) String description,
        String city,
        Integer foundedYear,
        String avatarUrl,
        String spotifyUrl,
        String youtubeUrl,
        String facebookUrl,
        String instagramUrl,
        String members
) {}
