package com.dinotoptrumps.game.adapters.in;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createGame_authenticated_returns201() throws Exception {
        String token = registerAndLogin("player1", "p1@test.com");

        mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.player1Id").isNotEmpty())
                .andExpect(jsonPath("$.player2Id").isEmpty());
    }

    @Test
    void createGame_unauthenticated_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/games"))
                .andExpect(status().isForbidden());
    }

    @Test
    void joinGame_validJoin_startsGame() throws Exception {
        String token1 = registerAndLogin("host", "host@test.com");
        String token2 = registerAndLogin("joiner", "joiner@test.com");

        String gameId = createGame(token1);

        mockMvc.perform(post("/api/v1/games/" + gameId + "/join")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.player2Id").isNotEmpty())
                .andExpect(jsonPath("$.yourHand").isArray())
                .andExpect(jsonPath("$.yourHand", hasSize(greaterThan(0))));
    }

    @Test
    void playTurn_validTurn_returnsTurnResult() throws Exception {
        String token1 = registerAndLogin("attacker", "att@test.com");
        String token2 = registerAndLogin("defender", "def@test.com");

        String gameId = createGame(token1);
        joinGame(gameId, token2);

        mockMvc.perform(post("/api/v1/games/" + gameId + "/turns")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stat", "STRENGTH"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chosenStat").value("STRENGTH"))
                .andExpect(jsonPath("$.turnNumber").value(1))
                .andExpect(jsonPath("$.player1StatValue").isNumber())
                .andExpect(jsonPath("$.player2StatValue").isNumber());
    }

    @Test
    void getGameState_asParticipant_returnsState() throws Exception {
        String token1 = registerAndLogin("viewer1", "v1@test.com");
        String gameId = createGame(token1);

        mockMvc.perform(get("/api/v1/games/" + gameId)
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getGameState_asNonParticipant_returns404() throws Exception {
        String token1 = registerAndLogin("owner", "own@test.com");
        String token3 = registerAndLogin("intruder", "int@test.com");

        String gameId = createGame(token1);

        mockMvc.perform(get("/api/v1/games/" + gameId)
                        .header("Authorization", "Bearer " + token3))
                .andExpect(status().isNotFound());
    }

    @Test
    void getGameState_nonExistentGame_returns404() throws Exception {
        String token = registerAndLogin("nobody", "no@test.com");

        mockMvc.perform(get("/api/v1/games/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCards_authenticated_returnsCards() throws Exception {
        String token = registerAndLogin("cardviewer", "cv@test.com");

        mockMvc.perform(get("/api/v1/cards")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(35)))
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].height").isNumber());
    }

    @Test
    void getLeaderboard_authenticated_returnsPlayers() throws Exception {
        String token = registerAndLogin("leaderuser", "lb@test.com");

        mockMvc.perform(get("/api/v1/leaderboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].eloRating").isNumber());
    }

    @Test
    void playTurn_invalidStat_returns400() throws Exception {
        String token1 = registerAndLogin("statplayer1", "sp1@test.com");
        String token2 = registerAndLogin("statplayer2", "sp2@test.com");

        String gameId = createGame(token1);
        joinGame(gameId, token2);

        mockMvc.perform(post("/api/v1/games/" + gameId + "/turns")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stat\":\"FIRE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvailableGames_returnsWaitingGames() throws Exception {
        String token1 = registerAndLogin("avail1", "av1@test.com");
        createGame(token1);

        mockMvc.perform(get("/api/v1/games/available")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getActiveGames_returnsPlayerGames() throws Exception {
        String token1 = registerAndLogin("active1", "ac1@test.com");
        createGame(token1);

        mockMvc.perform(get("/api/v1/games/active")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void forfeitGame_asParticipant_finishesGame() throws Exception {
        String token1 = registerAndLogin("forfhost", "fh@test.com");
        String token2 = registerAndLogin("forfjoiner", "fj@test.com");

        String gameId = createGame(token1);
        joinGame(gameId, token2);

        mockMvc.perform(post("/api/v1/games/" + gameId + "/forfeit")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"));
    }

    @Test
    void playTurn_nonParticipant_rejected() throws Exception {
        String token1 = registerAndLogin("turnhost", "th@test.com");
        String token2 = registerAndLogin("turnjoiner", "tj@test.com");
        String token3 = registerAndLogin("turnintruder", "ti@test.com");

        String gameId = createGame(token1);
        joinGame(gameId, token2);

        mockMvc.perform(post("/api/v1/games/" + gameId + "/turns")
                        .header("Authorization", "Bearer " + token3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stat", "STRENGTH"))))
                .andExpect(status().isForbidden());
    }

    private void registerUser(String username, String email) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "username", username,
                        "email", email,
                        "password", "password123"
                ))));
    }

    private String registerAndLogin(String username, String email) throws Exception {
        registerUser(username, email);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "password123"
                        ))))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asText();
    }

    private String createGame(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + token))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asText();
    }

    private void joinGame(String gameId, String token) throws Exception {
        mockMvc.perform(post("/api/v1/games/" + gameId + "/join")
                .header("Authorization", "Bearer " + token));
    }
}
