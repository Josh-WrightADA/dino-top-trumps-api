package com.dinotoptrumps.auth.adapters.out;

import com.dinotoptrumps.auth.domain.model.Report;
import com.dinotoptrumps.auth.ports.out.ForPersistingReports;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReportPersistenceAdapter implements ForPersistingReports {

    private final ReportJpaRepository reportJpaRepository;

    public ReportPersistenceAdapter(ReportJpaRepository reportJpaRepository) {
        this.reportJpaRepository = reportJpaRepository;
    }

    @Override
    public Report save(Report report) {
        ReportJpaEntity entity = ReportMapper.toEntity(report);
        ReportJpaEntity saved = reportJpaRepository.save(entity);
        return ReportMapper.toDomain(saved);
    }

    @Override
    public List<Report> findAll() {
        return reportJpaRepository.findAll().stream()
                .map(ReportMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Report> findById(UUID id) {
        return reportJpaRepository.findById(id)
                .map(ReportMapper::toDomain);
    }
}
