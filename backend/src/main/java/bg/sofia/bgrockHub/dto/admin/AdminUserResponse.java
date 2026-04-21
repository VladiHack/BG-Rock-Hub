package bg.sofia.bgrockHub.dto.admin;

import bg.sofia.bgrockHub.entity.enums.Role;
import java.time.LocalDateTime;

public record AdminUserResponse(
        Long id,
        String email,
        String username,
        Role role,
        String city,
        boolean isVerified,
        boolean isActive,
        LocalDateTime createdAt
) {}
