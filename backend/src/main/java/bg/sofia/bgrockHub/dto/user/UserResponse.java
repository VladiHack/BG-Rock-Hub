package bg.sofia.bgrockHub.dto.user;

import bg.sofia.bgrockHub.entity.enums.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String username,
        Role role,
        String avatarUrl,
        String bio,
        String city,
        boolean isVerified,
        LocalDateTime createdAt
) {}
