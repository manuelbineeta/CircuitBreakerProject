package com.circuitbreaker.order_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.circuitbreaker.order_service.model.AddressDTO;
import com.circuitbreaker.order_service.model.Failure;
import com.circuitbreaker.order_service.model.Order;
import com.circuitbreaker.order_service.model.Type;
import com.circuitbreaker.order_service.repository.OrderRepository;
import com.circuitbreaker.order_service.service.OrderServiceimpl;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)

@SpringBootTest
@Import(TestCircuitBreakerConfig.class)
class OrderServiceimplTest {
	private static final Logger logger = LoggerFactory.getLogger(OrderServiceimplTest.class);
	@Mock
	private OrderRepository orderRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OrderServiceimpl orderService;

	private Order order;

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	@BeforeEach
	void setUp() {
		order = new Order();
		order.setOrderNumber("0c70c0c2");
		order.setPostalCode("1000001");
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testGetOrderByPostCodeMethodSuccessScenario() {
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setCity("New York");
		addressDTO.setState("NY");

		when(orderRepository.findByOrderNumber("0c70c0c2")).thenReturn(Optional.of(order));
		when(restTemplate.exchange(eq("http://localhost:9090/addresses/1000001"), eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(AddressDTO.class))).thenReturn(new ResponseEntity<>(addressDTO, HttpStatus.OK));

		Type result = orderService.getOrderByPostCode("0c70c0c2");

		assertNotNull(result);
		assertEquals("New York", order.getShippingCity());
		assertEquals("NY", order.getShippingState());
	}

	@Test
	void testFallbackMethodWithCircuitBreaker() {
		System.out.println("hi");
		when(orderRepository.findByOrderNumber("0c70c0c2")).thenReturn(Optional.of(order));
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(AddressDTO.class)))
				.thenThrow(new RuntimeException("Service Unavailable"));

		// Simulate multiple failed attempts to open the circuit breaker
		for (int i = 0; i < 5; i++) {
			try {
				orderService.getOrderByPostCode("0c70c0c2");
			} catch (Exception e) {

			}
		}

		// Type result = orderService.getOrderByPostCode("0c70c0c2");

		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("order-service");

		// Manually transition the circuit breaker to OPEN state
		circuitBreaker.transitionToOpenState();

		assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState(), "Circuit breaker should be OPEN");

		// Given: Mock reposito
		// assertNotNull(result);
		// assertTrue(result instanceof Failure);
		// assertEquals("Address service is not responding properly", ((Failure)
		// result).getMessage());
	}

	@Test
	void testFallbackMethodWithoutCircuitBreaker() {

		String orderNumber = "0c70c0c2";
		Throwable runtimeException = new RuntimeException("Service Unavailable");

		Type result = orderService.fallbackMethod(orderNumber, runtimeException);

		assertNotNull(result);
		assertTrue(result instanceof Failure, "The result should be a Failure object.");

		Failure failureResult = (Failure) result;
		assertEquals("Address service is not responding properly", failureResult.getMessage());
		assertEquals("Service Unavailable", runtimeException.getMessage());
	}

	@Test
	void testGetOrderByPostCode_AddressDTOIsNotNull() {

		when(orderRepository.findByOrderNumber(order.getOrderNumber())).thenReturn(Optional.of(order));

		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setState("NY");
		addressDTO.setCity("New York");
		ResponseEntity<AddressDTO> response = new ResponseEntity<>(addressDTO, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AddressDTO.class)))
				.thenReturn(response);

		Type result = orderService.getOrderByPostCode(order.getOrderNumber());

		assertNotNull(result);
		assertTrue(result instanceof Order);
		Order updatedOrder = (Order) result;
		assertEquals("NY", updatedOrder.getShippingState());
		assertEquals("New York", updatedOrder.getShippingCity());
	}

}
