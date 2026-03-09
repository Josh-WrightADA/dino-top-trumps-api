package com.dinotoptrumps.auth.ports.in;

public interface ForResettingPassword {
    void requestPasswordReset(String email);
    void resetPassword(String token, String newRawPassword);
}
