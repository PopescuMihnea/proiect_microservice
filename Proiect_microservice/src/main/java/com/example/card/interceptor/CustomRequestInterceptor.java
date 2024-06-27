package com.example.card.interceptor;

import com.example.card.configuration.PropertiesConfiguration;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
class CustomRequestInterceptor implements RequestInterceptor {
    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    private final PropertiesConfiguration propertiesConfiguration;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }

        requestTemplate.header(X_FORWARDED_HOST, propertiesConfiguration.getGatewayServer());
    }
}