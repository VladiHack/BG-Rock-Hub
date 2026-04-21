package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.user.UpdateProfileRequest;
import bg.sofia.bgrockHub.dto.user.UserResponse;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.enums.Role;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
import bg.sofia.bgrockHub.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .username("testuser")
                .passwordHash("$2a$10$hashed")
                .role(Role.FAN)
                .city("Sofia")
                .bio("Rock fan")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setCurrentUser(User u) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                u.getEmail(), null, List.of());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    // ─── getById ──────────────────────────────────────────────────────────────

    @Test
    void shouldReturnUserResponseWhenGetByIdSucceeds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.role()).isEqualTo(Role.FAN);
    }

    @Test
    void shouldThrowNotFoundWhenGetByIdWithMissingUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getCurrentUserProfile ────────────────────────────────────────────────

    @Test
    void shouldReturnProfileWhenGetCurrentUserProfileSucceeds() {
        setCurrentUser(user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUserProfile();

        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.city()).isEqualTo("Sofia");
    }

    @Test
    void shouldThrowNotFoundWhenCurrentUserNotInDatabase() {
        setCurrentUser(user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUserProfile())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── updateProfile ────────────────────────────────────────────────────────

    @Test
    void shouldReturnUpdatedProfileWhenUpdateProfileSucceeds() {
        setCurrentUser(user);
        UpdateProfileRequest request = new UpdateProfileRequest(
                "newusername", "New bio", "Plovdiv", "https://avatar.url");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateProfile(request);

        assertThat(response).isNotNull();
        verify(userRepository).save(user);
        assertThat(user.getUsername()).isEqualTo("newusername");
        assertThat(user.getBio()).isEqualTo("New bio");
        assertThat(user.getCity()).isEqualTo("Plovdiv");
    }

    @Test
    void shouldUpdateOnlyProvidedFieldsWhenUpdateProfileWithNullFields() {
        setCurrentUser(user);
        UpdateProfileRequest request = new UpdateProfileRequest(null, null, "Varna", null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateProfile(request);

        assertThat(user.getUsername()).isEqualTo("testuser"); // unchanged
        assertThat(user.getCity()).isEqualTo("Varna");        // updated
        assertThat(user.getBio()).isEqualTo("Rock fan");      // unchanged
    }

    // ─── changePassword ───────────────────────────────────────────────────────

    @Test
    void shouldChangePasswordWhenOldPasswordIsCorrect() {
        setCurrentUser(user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "$2a$10$hashed")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("$2a$10$newhashed");

        userService.changePassword("oldPass", "newPass");

        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(user);
        assertThat(user.getPasswordHash()).isEqualTo("$2a$10$newhashed");
    }

    @Test
    void shouldThrowUnauthorizedWhenOldPasswordIsWrong() {
        setCurrentUser(user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "$2a$10$hashed")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("wrongPass", "newPass"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("парола");

        verify(userRepository, never()).save(any());
    }

    // ─── deleteAccount ────────────────────────────────────────────────────────

    @Test
    void shouldDeactivateUserWhenDeleteAccountSucceeds() {
        setCurrentUser(user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        userService.deleteAccount();

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }
}
