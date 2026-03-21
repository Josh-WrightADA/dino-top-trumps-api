package com.dinotoptrumps.auth.adapters.in;

import com.dinotoptrumps.auth.adapters.in.dto.AdminUserEntry;
import com.dinotoptrumps.auth.adapters.in.dto.MessageResponse;
import com.dinotoptrumps.auth.adapters.in.dto.ReportResponse;
import com.dinotoptrumps.auth.domain.model.Report;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.in.ForAdminOperations;
import com.dinotoptrumps.auth.ports.in.ForReportingUsers;
import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.ports.in.ForAdminGameOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final ForAdminOperations forAdminOperations;
    private final ForAdminGameOperations forAdminGameOperations;
    private final ForReportingUsers forReportingUsers;

    public AdminController(ForAdminOperations forAdminOperations,
                           ForAdminGameOperations forAdminGameOperations,
                           ForReportingUsers forReportingUsers) {
        this.forAdminOperations = forAdminOperations;
        this.forAdminGameOperations = forAdminGameOperations;
        this.forReportingUsers = forReportingUsers;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserEntry>> getAllUsers() {
        List<AdminUserEntry> users = forAdminOperations.getAllUsers().stream()
                .map(AdminUserEntry::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<AdminUserEntry> banUser(@PathVariable UUID id) {
        User user = forAdminOperations.banUser(id);
        return ResponseEntity.ok(AdminUserEntry.from(user));
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<AdminUserEntry> unbanUser(@PathVariable UUID id) {
        User user = forAdminOperations.unbanUser(id);
        return ResponseEntity.ok(AdminUserEntry.from(user));
    }

    @GetMapping("/games")
    public ResponseEntity<List<AdminGameEntry>> getAllGames() {
        List<AdminGameEntry> games = forAdminGameOperations.getAllGames().stream()
                .map(AdminGameEntry::from)
                .toList();
        return ResponseEntity.ok(games);
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<MessageResponse> deleteGame(@PathVariable UUID id) {
        forAdminGameOperations.deleteGame(id);
        return ResponseEntity.ok(new MessageResponse("Game deleted"));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        List<ReportResponse> reports = forReportingUsers.getAllReports().stream()
                .map(ReportResponse::from)
                .toList();
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/reports/{id}/dismiss")
    public ResponseEntity<ReportResponse> dismissReport(@PathVariable UUID id) {
        Report report = forReportingUsers.dismissReport(id);
        return ResponseEntity.ok(ReportResponse.from(report));
    }

    public record AdminGameEntry(
            UUID id,
            String status,
            UUID player1Id,
            UUID player2Id,
            Instant createdAt
    ) {
        public static AdminGameEntry from(Game game) {
            return new AdminGameEntry(
                    game.getId(),
                    game.getStatus().name(),
                    game.getPlayer1Id(),
                    game.getPlayer2Id(),
                    game.getCreatedAt()
            );
        }
    }
}
