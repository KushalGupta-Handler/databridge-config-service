package com.databridge.configservice.mapper;

import com.databridge.configservice.domain.JobConfig;
import com.databridge.configservice.dto.JobConfigDto;

public final class JobConfigMapper {

    private JobConfigMapper() {}

    public static JobConfigDto toDto(JobConfig entity) {
        if (entity == null) return null;
        return JobConfigDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .value(entity.getValue())
                .build();
    }

    public static JobConfig toEntity(JobConfigDto dto) {
        if (dto == null) return null;
        JobConfig entity = new JobConfig();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setValue(dto.getValue());
        return entity;
    }
}

