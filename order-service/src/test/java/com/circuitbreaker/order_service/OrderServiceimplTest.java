package com.circuitbreaker.order_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.circuitbreaker.order_service.model.AddressDTO;
import com.circuitbreaker.order_service.model.Failure;
import com.circuitbreaker.order_service.model.Order;
import com.circuitbreaker.order_service.model.Type;
import com.circuitbreaker.order_service.repository.OrderRepository;
import com.circuitbreaker.order_service.service.OrderServiceimpl;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest
class OrderServiceimplTest {

	@SpyBean // Earlier used @Injectmock and @Mock which doesnt trigger circuit breaker
				// fallback
				// method// since Resilience4j’s @CircuitBreaker annotation relies on Spring AOP
				// (Aspect-Oriented Programming) to create a proxy around the method.
	// Since @InjectMocks creates a raw instance, the annotated method is called
	// directly, skipping the circuit breaker logic.

	private OrderServiceimpl orderService;
	@MockBean
	private OrderRepository orderRepository;

	@MockBean
	private RestTemplate restTemplate;

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	private Order order;

	@BeforeEach
	void setUp() {

		order = new Order();
		order.setOrderNumber("0c70c0c2");
		order.setPostalCode("1000001");
	}

	/** ✅ 1. Success Scenario - Address Service Works */
	@Test
	void testGetOrderByPostCode_Success() {
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setCity("New York");
		addressDTO.setState("NY");

		when(orderRepository.findByOrderNumber("0c70c0c2")).thenReturn(Optional.of(order));
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AddressDTO.class)))
				.thenReturn(new ResponseEntity<>(addressDTO, HttpStatus.OK));

		Type result = orderService.getOrderByPostCode("0c70c0c2");

		assertTrue(result instanceof Order);
		assertEquals("New York", ((Order) result).getShippingCity());
		assertEquals("NY", ((Order) result).getShippingState());
	}

	/**
	 * Failure Scenario - Address Service Fails & Circuit Breaker Triggers
	 * Fallback
	 */
	@Test
	void testCircuitBreakerFallbackTriggered() {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("order-service");

		when(orderRepository.findByOrderNumber("12345")).thenReturn(Optional.of(order));
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(AddressDTO.class)))
				.thenThrow(new RuntimeException("Address service unavailable"));

		// Simulate multiple failures to enable the circuit breaker
		for (int i = 0; i < 5; i++) {
			try {
				orderService.getOrderByPostCode("12345");
			} catch (Exception e) {
			}
		}

		// Ensure Circuit Breaker is OPEN
		assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

		// Call service again (should trigger fallback)
		Type result = orderService.getOrderByPostCode("12345");

		assertNotNull(result);
		assertTrue(result instanceof Failure);
		assertEquals("Address service is not responding properly", ((Failure) result).getMessage());
	}

}
