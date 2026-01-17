package com.pesexpo.productservice.config;

import com.pesexpo.productservice.client.OrderClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public OrderClient orderClient() {
        // OpenTelemetry Spring Boot Starter auto-instruments RestClient
        // Trace context propagation happens automatically via bytecode instrumentation
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:9003")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(OrderClient.class);
    }

}
