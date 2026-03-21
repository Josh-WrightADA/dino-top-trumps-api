package com.dinotoptrumps.auth.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, UUID> {
}
