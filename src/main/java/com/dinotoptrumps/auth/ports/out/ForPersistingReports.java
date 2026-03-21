package com.dinotoptrumps.auth.ports.out;

import com.dinotoptrumps.auth.domain.model.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForPersistingReports {
    Report save(Report report);
    List<Report> findAll();
    Optional<Report> findById(UUID id);
}
