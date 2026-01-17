package com.pesexpo.orderservice.config;

import com.pesexpo.orderservice.client.ProductClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public ProductClient productClient() {
        // OpenTelemetry Spring Boot Starter auto-instruments RestClient
        // Trace context propagation happens automatically via bytecode instrumentation
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:9002")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(ProductClient.class);
    }

}
