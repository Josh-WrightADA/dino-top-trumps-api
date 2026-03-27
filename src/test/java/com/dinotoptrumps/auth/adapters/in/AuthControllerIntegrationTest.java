package com.dinotoptrumps.auth.adapters.in;

import com.dinotoptrumps.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void register_validRequest_returns201() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "testuser",
                                "email", "test@example.com",
                                "password", "password123"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void register_duplicateUsername_returns409() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        registerAndLogin("loginuser", "login@example.com", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "loginuser",
                                "password", "password123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        registerAndLogin("wrongpw", "wrongpw@example.com", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "wrongpw",
                                "password", "wrongpassword"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProfile_withValidToken_returnsProfile() throws Exception {
        String token = registerAndLogin("profuser", "prof@example.com", "password123");

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("profuser"))
                .andExpect(jsonPath("$.leaguePoints").value(0))
                .andExpect(jsonPath("$.rankTier").value("CARNIVORE"));
    }

    @Test
    void getProfile_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void changePassword_correctCurrentPassword_returns200() throws Exception {
        String token = registerAndLogin("changepw", "changepw@example.com", "password123");

        mockMvc.perform(put("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "currentPassword", "password123",
                                "newPassword", "newpassword456"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    void changePassword_wrongCurrentPassword_returns400() throws Exception {
        String token = registerAndLogin("badpwuser", "badpwuser@example.com", "password123");

        mockMvc.perform(put("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "currentPassword", "wrongpassword",
                                "newPassword", "newpassword456"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_newPasswordTooShort_returns400() throws Exception {
        String token = registerAndLogin("shortpwuser", "shortpwuser@example.com", "password123");

        mockMvc.perform(put("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "currentPassword", "password123",
                                "newPassword", "short"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAccount_withValidToken_returns204() throws Exception {
        String token = registerAndLogin("deleteuser", "deleteuser@example.com", "password123");

        mockMvc.perform(delete("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"password\":\"password123\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAccount_wrongPassword_returns400() throws Exception {
        String token = registerAndLogin("deluser2", "deluser2@example.com", "password123");

        mockMvc.perform(delete("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"password\":\"wrongpassword\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAccount_noToken_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPublicProfile_existingUser_returnsPublicFields() throws Exception {
        String token = registerAndLogin("publicuser", "publicuser@example.com", "password123");
        String userId = extractUserIdFromToken(token);

        mockMvc.perform(get("/api/v1/auth/players/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("publicuser"))
                .andExpect(jsonPath("$.leaguePoints").value(0))
                .andExpect(jsonPath("$.rankTier").value("CARNIVORE"));
    }

    @Test
    void getPublicProfile_nonExistentUser_returns404() throws Exception {
        String token = registerAndLogin("requester2", "requester2@example.com", "password123");

        mockMvc.perform(get("/api/v1/auth/players/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private String extractUserIdFromToken(String jwtToken) throws Exception {
        String[] parts = jwtToken.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        return objectMapper.readTree(payload).get("sub").asText();
    }
}
