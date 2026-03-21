package com.dinotoptrumps.auth.adapters.out;

import com.dinotoptrumps.auth.domain.model.Report;
import com.dinotoptrumps.auth.domain.model.ReportStatus;

public class ReportMapper {

    public static ReportJpaEntity toEntity(Report report) {
        ReportJpaEntity entity = new ReportJpaEntity();
        entity.setId(report.getId());
        entity.setReporterId(report.getReporterId());
        entity.setReportedUserId(report.getReportedUserId());
        entity.setReason(report.getReason());
        entity.setStatus(report.getStatus().name());
        entity.setCreatedAt(report.getCreatedAt());
        return entity;
    }

    public static Report toDomain(ReportJpaEntity entity) {
        return new Report(
                entity.getId(),
                entity.getReporterId(),
                entity.getReportedUserId(),
                entity.getReason(),
                ReportStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}
