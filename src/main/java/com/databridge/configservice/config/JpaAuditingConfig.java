package com.databridge.configservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Enables Spring Data JPA auditing annotations (@CreatedDate, @LastModifiedDate, etc.)
}

