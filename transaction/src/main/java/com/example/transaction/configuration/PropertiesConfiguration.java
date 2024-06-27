package com.example.transaction.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("transaction")
@Data
public class PropertiesConfiguration {
    private boolean convertToUsd;
}
