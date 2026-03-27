package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.exception.UserNotFoundException;
import com.dinotoptrumps.auth.domain.model.Report;
import com.dinotoptrumps.auth.ports.in.ForReportingUsers;
import com.dinotoptrumps.auth.ports.out.ForPersistingReports;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class ReportService implements ForReportingUsers {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ForPersistingReports reportRepository;
    private final ForPersistingUsers userRepository;

    public ReportService(ForPersistingReports reportRepository, ForPersistingUsers userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Report reportUser(UUID reporterId, UUID reportedUserId, String reason) {
        if (reporterId.equals(reportedUserId)) {
            throw new IllegalArgumentException("Cannot report yourself");
        }
        userRepository.findById(reportedUserId)
                .orElseThrow(() -> new UserNotFoundException("Reported user not found"));

        Report report = Report.create(reporterId, reportedUserId, reason);
        Report saved = reportRepository.save(report);
        log.info("event_type=USER_REPORTED reporterId={} reportedUserId={}", reporterId, reportedUserId);
        return saved;
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Report dismissReport(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new UserNotFoundException("Report not found"));
        report.dismiss();
        Report saved = reportRepository.save(report);
        log.info("event_type=REPORT_DISMISSED reportId={}", reportId);
        return saved;
    }
}
