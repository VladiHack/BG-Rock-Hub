package bg.sofia.bgrockHub.dto.auth;

import bg.sofia.bgrockHub.entity.enums.Role;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String username,
        String email,
        Role role
) {}
