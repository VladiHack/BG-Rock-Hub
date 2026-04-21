package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.user.UpdateProfileRequest;
import bg.sofia.bgrockHub.dto.user.UserResponse;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
import bg.sofia.bgrockHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Потребител", id)));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        return toResponse(getCurrentUser());
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        if (request.username() != null) user.setUsername(request.username());
        if (request.bio() != null) user.setBio(request.bio());
        if (request.city() != null) user.setCity(request.city());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new UnauthorizedException("Старата парола е грешна");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount() {
        User user = getCurrentUser();
        user.setActive(false);
        userRepository.save(user);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен"));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getUsername(), u.getRole(),
                u.getAvatarUrl(), u.getBio(), u.getCity(), u.isVerified(), u.getCreatedAt());
    }
}
