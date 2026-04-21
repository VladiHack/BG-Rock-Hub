package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.auth.AuthResponse;
import bg.sofia.bgrockHub.dto.auth.LoginRequest;
import bg.sofia.bgrockHub.dto.auth.RefreshTokenRequest;
import bg.sofia.bgrockHub.dto.auth.RegisterRequest;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.enums.Role;
import bg.sofia.bgrockHub.exception.DuplicateResourceException;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.repository.UserRepository;
import bg.sofia.bgrockHub.security.JwtService;
import bg.sofia.bgrockHub.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsServiceImpl userDetailsService;

    @InjectMocks private AuthService authService;

    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .passwordHash("$2a$10$hashed")
                .role(Role.FAN)
                .city("Sofia")
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com", "$2a$10$hashed",
                List.of(new SimpleGrantedAuthority("ROLE_FAN")));
    }

    // ─── register ─────────────────────────────────────────────────────────────

    @Test
    void shouldReturnAuthResponseWhenRegisterSucceeds() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "testuser", "password123", Role.FAN, "Sofia");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.role()).isEqualTo(Role.FAN);
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void shouldThrowDuplicateExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "testuser", "password123", Role.FAN, "Sofia");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Имейлът");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateExceptionWhenUsernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest(
                "new@example.com", "testuser", "password123", Role.FAN, "Sofia");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Потребителското");

        verify(userRepository, never()).save(any());
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    void shouldReturnAuthResponseWhenLoginSucceeds() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.username()).isEqualTo("testuser");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowNotFoundWhenLoginWithUnknownEmail() {
        LoginRequest request = new LoginRequest("unknown@example.com", "password");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── refreshToken ─────────────────────────────────────────────────────────

    @Test
    void shouldReturnNewTokensWhenRefreshTokenIsValid() {
        String oldRefresh = "old-refresh-token";
        testUser.setRefreshToken(oldRefresh);
        RefreshTokenRequest request = new RefreshTokenRequest(oldRefresh);

        when(userRepository.findByRefreshToken(oldRefresh)).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(oldRefresh, userDetails)).thenReturn(true);
        when(jwtService.generateToken(userDetails)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("new-refresh");

        AuthResponse response = authService.refreshToken(request);

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void shouldThrowNotFoundWhenRefreshTokenDoesNotExist() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");

        when(userRepository.findByRefreshToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Невалиден refresh token");
    }

    @Test
    void shouldThrowNotFoundWhenRefreshTokenIsExpired() {
        String expiredToken = "expired-token";
        testUser.setRefreshToken(expiredToken);
        RefreshTokenRequest request = new RefreshTokenRequest(expiredToken);

        when(userRepository.findByRefreshToken(expiredToken)).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(expiredToken, userDetails)).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("изтекъл");
    }
}
