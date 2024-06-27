package com.example.gatewayservice.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class ResponseFilter {


    public static final String CORRELATION_ID = "awbd-id";
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
                            HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                            String correlationId = null;
                            if (requestHeaders.get(CORRELATION_ID) != null)
                                correlationId = requestHeaders.get(CORRELATION_ID).stream().findFirst().get();

                            log.info("Updated the correlation id to response headers: {}", correlationId);
                            exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);
                        }
                )
        );
    }
}
