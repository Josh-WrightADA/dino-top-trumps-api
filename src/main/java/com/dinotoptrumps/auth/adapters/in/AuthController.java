package com.dinotoptrumps.auth.adapters.in;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // TODO: Inject ForRegistering, ForAuthenticating, ForManagingProfile, ForResettingPassword

    @PostMapping("/register")
    public void register() {
        // TODO: Accept registration DTO, delegate to ForRegistering
    }

    @PostMapping("/login")
    public void login() {
        // TODO: Accept login DTO, delegate to ForAuthenticating, return JWT
    }

    @GetMapping("/me")
    public void getProfile() {
        // TODO: Get authenticated user profile
    }

    @PutMapping("/me")
    public void updateProfile() {
        // TODO: Update display name
    }

    @PostMapping("/forgot-password")
    public void forgotPassword() {
        // TODO: Accept email, delegate to ForResettingPassword
    }

    @PostMapping("/reset-password")
    public void resetPassword() {
        // TODO: Accept token + new password, delegate to ForResettingPassword
    }
}
