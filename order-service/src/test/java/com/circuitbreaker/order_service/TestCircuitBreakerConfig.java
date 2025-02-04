package com.circuitbreaker.order_service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

import com.circuitbreaker.order_service.service.OrderServiceimpl;

@TestConfiguration
@Import(OrderServiceimpl.class)
public class TestCircuitBreakerConfig {

    @Bean
    @Primary
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        // Custom CircuitBreakerConfig for tests
        CircuitBreakerConfig customConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(4) // Number of calls before the failure rate is checked
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(50) // Open when 50% of calls fail
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .automaticTransitionFromOpenToHalfOpenEnabled(true) // Allow auto-reset
                .recordExceptions(RuntimeException.class) // Consider RuntimeException as failure
                .build();

        return CircuitBreakerRegistry.of(customConfig);
    }

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("order-service");
    }

}
