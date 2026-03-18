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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final ForRegistering forRegistering;
    private final ForAuthenticating forAuthenticating;
    private final ForManagingProfile forManagingProfile;
    private final ForResettingPassword forResettingPassword;
    private final ForViewingPublicProfile forViewingPublicProfile;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(ForRegistering forRegistering,
                          ForAuthenticating forAuthenticating,
                          ForManagingProfile forManagingProfile,
                          ForResettingPassword forResettingPassword,
                          ForViewingPublicProfile forViewingPublicProfile,
                          JwtTokenProvider jwtTokenProvider) {
        this.forRegistering = forRegistering;
        this.forAuthenticating = forAuthenticating;
        this.forManagingProfile = forManagingProfile;
        this.forResettingPassword = forResettingPassword;
        this.forViewingPublicProfile = forViewingPublicProfile;
        this.jwtTokenProvider = jwtTokenProvider;
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
