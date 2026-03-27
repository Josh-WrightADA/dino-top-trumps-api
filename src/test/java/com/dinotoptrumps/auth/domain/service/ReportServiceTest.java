package com.dinotoptrumps.auth.domain.service;


import com.dinotoptrumps.auth.domain.model.AccountStatus;
import com.dinotoptrumps.auth.domain.model.Report;
import com.dinotoptrumps.auth.domain.model.ReportStatus;
import com.dinotoptrumps.auth.domain.model.Role;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.out.ForPersistingReports;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    private ForPersistingReports reportRepository;
    private ForPersistingUsers userRepository;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportRepository = mock(ForPersistingReports.class);
        userRepository = mock(ForPersistingUsers.class);
        reportService = new ReportService(reportRepository, userRepository);
    }

    @Nested
    class ReportUser {

        @Test
        void reportUser_createsReport() {
            UUID reporterId = UUID.randomUUID();
            UUID reportedId = UUID.randomUUID();
            User reported = TestFixtures.createUser(reportedId, "reported", Role.PLAYER, AccountStatus.ACTIVE);
            when(userRepository.findById(reportedId)).thenReturn(Optional.of(reported));
            when(reportRepository.save(any(Report.class))).thenAnswer(inv -> inv.getArgument(0));

            Report result = reportService.reportUser(reporterId, reportedId, "This user is abusive");

            assertEquals(reporterId, result.getReporterId());
            assertEquals(reportedId, result.getReportedUserId());
            assertEquals(ReportStatus.PENDING, result.getStatus());
        }

        @Test
        void reportUser_cannotReportSelf() {
            UUID userId = UUID.randomUUID();

            assertThrows(IllegalArgumentException.class,
                    () -> reportService.reportUser(userId, userId, "trying to self report"));
        }
    }

    @Nested
    class DismissReport {

        @Test
        void dismissReport_dismissesPendingReport() {
            UUID reportId = UUID.randomUUID();
            Report report = Report.create(UUID.randomUUID(), UUID.randomUUID(), "some reason");
            when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
            when(reportRepository.save(any(Report.class))).thenAnswer(inv -> inv.getArgument(0));

            Report result = reportService.dismissReport(reportId);

            assertEquals(ReportStatus.DISMISSED, result.getStatus());
        }
    }
}
