package com.databridge.configservice.repository;

import com.databridge.configservice.domain.JobConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobConfigRepository extends JpaRepository<JobConfig, Long> {
    Optional<JobConfig> findByName(String name);
}

