package com.databridge.configservice.service;

import com.databridge.configservice.dto.JobConfigDto;

import java.util.List;
import java.util.Optional;

public interface ConfigService {
    JobConfigDto create(JobConfigDto dto);
    Optional<JobConfigDto> findByName(String name);
    List<JobConfigDto> findAll();
}

