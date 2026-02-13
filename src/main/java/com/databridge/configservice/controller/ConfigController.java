package com.databridge.configservice.controller;

import com.databridge.configservice.controller.dto.BaseResponse;
import com.databridge.configservice.dto.JobConfigDto;
import com.databridge.configservice.service.ConfigService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/configs", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConfigController {

    private static final Logger log = LoggerFactory.getLogger(ConfigController.class);

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<JobConfigDto>> create(@Valid @RequestBody JobConfigDto dto) {
        JobConfigDto created = configService.create(dto);
        return ResponseEntity.ok(new BaseResponse<>(true, created, null));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<JobConfigDto>>> list() {
        List<JobConfigDto> all = configService.findAll();
        return ResponseEntity.ok(new BaseResponse<>(true, all, null));
    }

    @GetMapping("/by-name")
    public ResponseEntity<BaseResponse<JobConfigDto>> getByName(@RequestParam String name) {
        return configService.findByName(name)
                .map(dto -> ResponseEntity.ok(new BaseResponse<>(true, dto, null)))
                .orElseGet(() -> ResponseEntity.ok(new BaseResponse<>(false, null, "Not found")));
    }
}

