package com.dinotoptrumps.auth.adapters.in.dto;

import com.dinotoptrumps.auth.domain.model.Report;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID reporterId,
        UUID reportedUserId,
        String reason,
        String status,
        Instant createdAt
) {
    public static ReportResponse from(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReporterId(),
                report.getReportedUserId(),
                report.getReason(),
                report.getStatus().name(),
                report.getCreatedAt()
        );
    }
}
