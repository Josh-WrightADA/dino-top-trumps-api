package com.dinotoptrumps.auth.adapters.in;

import com.dinotoptrumps.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class AdminControllerIntegrationTest extends IntegrationTestBase {

    // player1 is seeded by V11 and promoted to ADMIN by V15
    private static final String ADMIN_USERNAME = "player1";
    private static final String ADMIN_PASSWORD = "password123";

    private String loginAsAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", ADMIN_USERNAME,
                                "password", ADMIN_PASSWORD
                        ))))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asText();
    }

    @Test
    void getAllUsers_asAdmin_returns200() throws Exception {
        String adminToken = loginAsAdmin();

        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllUsers_asNonAdmin_returns403() throws Exception {
        String playerToken = registerAndLogin("regularuser", "regular@test.com", "password123");

        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + playerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void banUser_asAdmin_bannedStatusReturned() throws Exception {
        String adminToken = loginAsAdmin();
        String targetToken = registerAndLogin("targetuser", "target@test.com", "password123");
        String targetId = extractUserIdFromToken(targetToken);

        mockMvc.perform(put("/api/v1/admin/users/" + targetId + "/ban")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BANNED"));
    }

    @Test
    void banUser_asNonAdmin_returns403() throws Exception {
        String playerToken = registerAndLogin("player", "player@test.com", "password123");
        String targetToken = registerAndLogin("target2", "target2@test.com", "password123");
        String targetId = extractUserIdFromToken(targetToken);

        mockMvc.perform(put("/api/v1/admin/users/" + targetId + "/ban")
                        .header("Authorization", "Bearer " + playerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getReports_asAdmin_returns200() throws Exception {
        String adminToken = loginAsAdmin();

        mockMvc.perform(get("/api/v1/admin/reports")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private String extractUserIdFromToken(String jwtToken) throws Exception {
        String[] parts = jwtToken.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        return objectMapper.readTree(payload).get("sub").asText();
    }
}
