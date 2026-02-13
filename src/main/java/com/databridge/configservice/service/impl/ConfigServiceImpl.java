package com.databridge.configservice.service.impl;

import com.databridge.configservice.domain.JobConfig;
import com.databridge.configservice.dto.JobConfigDto;
import com.databridge.configservice.mapper.JobConfigMapper;
import com.databridge.configservice.repository.JobConfigRepository;
import com.databridge.configservice.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConfigServiceImpl implements ConfigService {

    private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private final JobConfigRepository repository;

    public ConfigServiceImpl(JobConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public JobConfigDto create(JobConfigDto dto) {
        // No business logic yet — simple persist
        JobConfig entity = JobConfigMapper.toEntity(dto);
        JobConfig saved = repository.save(entity);
        log.debug("Saved JobConfig id={}", saved.getId());
        return JobConfigMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JobConfigDto> findByName(String name) {
        return repository.findByName(name).map(JobConfigMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobConfigDto> findAll() {
        return repository.findAll().stream().map(JobConfigMapper::toDto).collect(Collectors.toList());
    }
}

