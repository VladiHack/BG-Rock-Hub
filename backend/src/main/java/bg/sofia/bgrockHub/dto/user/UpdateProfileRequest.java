package bg.sofia.bgrockHub.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 3, max = 50) String username,
        @Size(max = 500) String bio,
        String city,
        String avatarUrl
) {}
