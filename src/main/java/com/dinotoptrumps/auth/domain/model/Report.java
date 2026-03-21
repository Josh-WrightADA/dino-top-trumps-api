package com.dinotoptrumps.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Report {

    private final UUID id;
    private final UUID reporterId;
    private final UUID reportedUserId;
    private final String reason;
    private ReportStatus status;
    private final Instant createdAt;

    public Report(UUID id, UUID reporterId, UUID reportedUserId, String reason,
                  ReportStatus status, Instant createdAt) {
        this.id = id;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Report create(UUID reporterId, UUID reportedUserId, String reason) {
        return new Report(
                UUID.randomUUID(),
                reporterId,
                reportedUserId,
                reason,
                ReportStatus.PENDING,
                Instant.now()
        );
    }

    public void dismiss() {
        this.status = ReportStatus.DISMISSED;
    }

    public void markReviewed() {
        this.status = ReportStatus.REVIEWED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public UUID getReportedUserId() {
        return reportedUserId;
    }

    public String getReason() {
        return reason;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
