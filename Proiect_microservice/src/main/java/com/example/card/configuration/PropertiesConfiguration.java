package com.example.card.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("card")
@Data
public class PropertiesConfiguration {
    private boolean showTransactions;
    private String gatewayServer;
}
