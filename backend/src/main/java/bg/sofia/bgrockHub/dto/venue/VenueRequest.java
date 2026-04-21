package bg.sofia.bgrockHub.dto.venue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VenueRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank String address,
        @NotBlank String city,
        @Size(max = 2000) String description,
        Integer capacity,
        String phone,
        String website,
        String coverImgUrl
) {}
