package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.Report;

import java.util.List;
import java.util.UUID;

public interface ForReportingUsers {
    Report reportUser(UUID reporterId, UUID reportedUserId, String reason);
    List<Report> getAllReports();
    Report dismissReport(UUID reportId);
}
