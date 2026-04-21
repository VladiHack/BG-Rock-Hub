package bg.sofia.bgrockHub.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "bgrockHubSuperSecretKeyForJWTTokens2024!";
    private static final long EXPIRATION = 86_400_000L;       // 1 day
    private static final long REFRESH_EXPIRATION = 604_800_000L; // 7 days
    private static final long EXPIRED = -1000L;               // already expired

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);

        userDetails = new User("test@example.com", "password",
                List.of(new SimpleGrantedAuthority("ROLE_FAN")));
    }

    // ─── generateToken ────────────────────────────────────────────────────────

    @Test
    void shouldReturnTokenWhenGeneratingAccessToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
    }

    @Test
    void shouldReturnTokenWhenGeneratingRefreshToken() {
        String token = jwtService.generateRefreshToken(userDetails);

        assertThat(token).isNotBlank();
    }

    @Test
    void shouldReturnDifferentTokensForAccessAndRefresh() {
        String access = jwtService.generateToken(userDetails);
        String refresh = jwtService.generateRefreshToken(userDetails);

        assertThat(access).isNotEqualTo(refresh);
    }

    // ─── extractUsername ──────────────────────────────────────────────────────

    @Test
    void shouldReturnCorrectUsernameWhenExtractingFromToken() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("test@example.com");
    }

    // ─── isTokenValid ─────────────────────────────────────────────────────────

    @Test
    void shouldReturnTrueWhenTokenIsValidForUser() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenTokenBelongsToDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = new User("other@example.com", "password",
                List.of(new SimpleGrantedAuthority("ROLE_FAN")));

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRED);
        String token = jwtService.generateToken(userDetails);

        assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldReturnUsernameFromRefreshToken() {
        String refresh = jwtService.generateRefreshToken(userDetails);

        assertThat(jwtService.extractUsername(refresh)).isEqualTo("test@example.com");
    }
}
