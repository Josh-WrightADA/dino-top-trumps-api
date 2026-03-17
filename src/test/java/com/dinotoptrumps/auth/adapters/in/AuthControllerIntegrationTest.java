package com.dinotoptrumps.auth.adapters.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        registerUser("loginuser", "login@example.com", "password123");

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
        registerUser("wrongpw", "wrongpw@example.com", "password123");

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
                .andExpect(jsonPath("$.eloRating").value(1000));
    }

    @Test
    void getProfile_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isForbidden());
    }

    private void registerUser(String username, String email, String password) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "username", username,
                        "email", email,
                        "password", password
                ))));
    }

    private String registerAndLogin(String username, String email, String password) throws Exception {
        registerUser(username, email, password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password
                        ))))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asText();
    }
}
