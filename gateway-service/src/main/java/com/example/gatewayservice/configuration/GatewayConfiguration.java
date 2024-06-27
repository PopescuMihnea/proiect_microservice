package com.example.gatewayservice.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/api/card/**")
                        .filters(f -> f
                                .rewritePath("/api/card/(?<segment>.*)", "/api/card/${segment}")
                                .addResponseHeader("X-Response-Time", new Date().toString()))
                        .uri("lb://CARD"))
                .route(p -> p
                        .path("/api/transaction/**")
                        .filters(f -> f
                                .rewritePath("/api/transaction/(?<segment>.*)", "/api/transaction/${segment}")
                                .addResponseHeader("X-Response-Time", new Date().toString()))
                        .uri("lb://TRANSACTION"))
                .build();
    }
}
