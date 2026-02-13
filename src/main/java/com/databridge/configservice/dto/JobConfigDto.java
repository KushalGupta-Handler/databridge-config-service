package com.databridge.configservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobConfigDto {
    private Long id;

    @NotBlank(message = "name must not be blank")
    private String name;

    private String value;
}

