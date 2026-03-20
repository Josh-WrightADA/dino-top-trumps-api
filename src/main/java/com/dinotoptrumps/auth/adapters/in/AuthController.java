package com.dinotoptrumps.auth.adapters.in;

import com.dinotoptrumps.auth.adapters.in.dto.ChangePasswordRequest;
import com.dinotoptrumps.auth.adapters.in.dto.ForgotPasswordRequest;
import com.dinotoptrumps.auth.adapters.in.dto.LoginRequest;
import com.dinotoptrumps.auth.adapters.in.dto.LoginResponse;
import com.dinotoptrumps.auth.adapters.in.dto.MessageResponse;
import com.dinotoptrumps.auth.adapters.in.dto.ProfileResponse;
import com.dinotoptrumps.auth.adapters.in.dto.RegisterRequest;
import com.dinotoptrumps.auth.adapters.in.dto.ResetPasswordRequest;
import com.dinotoptrumps.auth.adapters.in.dto.UpdateProfileRequest;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.domain.model.UserProfile;
import com.dinotoptrumps.auth.ports.in.ForAuthenticating;
import com.dinotoptrumps.auth.ports.in.ForManagingProfile;
import com.dinotoptrumps.auth.ports.in.ForRegistering;
import com.dinotoptrumps.auth.ports.in.ForResettingPassword;
import com.dinotoptrumps.auth.ports.in.ForViewingPublicProfile;
import com.dinotoptrumps.auth.ports.out.ForStoringMedia;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final long MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final ForRegistering forRegistering;
    private final ForAuthenticating forAuthenticating;
    private final ForManagingProfile forManagingProfile;
    private final ForResettingPassword forResettingPassword;
    private final ForViewingPublicProfile forViewingPublicProfile;
    private final JwtTokenProvider jwtTokenProvider;
    private final ForStoringMedia forStoringMedia;

    public AuthController(ForRegistering forRegistering,
                          ForAuthenticating forAuthenticating,
                          ForManagingProfile forManagingProfile,
                          ForResettingPassword forResettingPassword,
                          ForViewingPublicProfile forViewingPublicProfile,
                          JwtTokenProvider jwtTokenProvider,
                          ForStoringMedia forStoringMedia) {
        this.forRegistering = forRegistering;
        this.forAuthenticating = forAuthenticating;
        this.forManagingProfile = forManagingProfile;
        this.forResettingPassword = forResettingPassword;
        this.forViewingPublicProfile = forViewingPublicProfile;
        this.jwtTokenProvider = jwtTokenProvider;
        this.forStoringMedia = forStoringMedia;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        forRegistering.register(request.username(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = forAuthenticating.authenticate(request.username(), request.password());
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = forManagingProfile.getProfile(userId);
        UserProfile profile = UserProfile.fromUser(user);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(Authentication authentication,
                                                         @Valid @RequestBody UpdateProfileRequest request) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = forManagingProfile.updateDisplayName(userId, request.displayName());
        UserProfile profile = UserProfile.fromUser(user);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<ProfileResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid file type. Allowed: jpeg, png, webp");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be 2MB or smaller");
        }

        byte[] imageData;
        try {
            imageData = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read uploaded file");
        }

        String avatarUrl = forStoringMedia.uploadAvatar(userId, imageData, contentType);
        User updated = forManagingProfile.updateAvatar(userId, avatarUrl);
        UserProfile profile = UserProfile.fromUser(updated);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PostMapping("/me/avatar/dino")
    public ResponseEntity<ProfileResponse> setDinoAvatar(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imageUrl is required");
        }
        User updated = forManagingProfile.updateAvatar(userId, imageUrl);
        UserProfile profile = UserProfile.fromUser(updated);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PutMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(Authentication authentication,
                                                          @Valid @RequestBody ChangePasswordRequest request) {
        UUID userId = (UUID) authentication.getPrincipal();
        forManagingProfile.changePassword(userId, request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        forManagingProfile.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<ProfileResponse> getPublicProfile(@PathVariable UUID id) {
        UserProfile profile = forViewingPublicProfile.getPublicProfile(id);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forResettingPassword.requestPasswordReset(request.email());
        return ResponseEntity.ok(new MessageResponse("If that email exists, a reset link has been sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        forResettingPassword.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Password reset successful"));
    }
}
