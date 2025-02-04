package com.circuitbreaker.address_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.circuitbreaker.address_service.model.Address;
import com.circuitbreaker.address_service.repository.AddressRepository;
import com.circuitbreaker.address_service.service.AddressServiceImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

	@Mock
	private AddressRepository addressRepository;

	@InjectMocks
	private AddressServiceImpl addressService;

	private Address address;

	@BeforeEach
	void setUp() {
		address = new Address();
		address.setPostalCode("10001");
		address.setCity("New York");
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testGetAddressByPostalCodeSuccessScenario() {
		when(addressRepository.findByPostalCode("10001")).thenReturn(Optional.of(address));

		Address foundAddress = addressService.getAddressByPostalCode("10001");
		assertEquals("10001", foundAddress.getPostalCode());
		assertEquals("New York", foundAddress.getCity());
	}

	@Test
	void testGetAddressByPostalCodeIfAddressIsNotFound() {
		when(addressRepository.findByPostalCode("99999")).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> {
			addressService.getAddressByPostalCode("99999");
		});

		assertEquals("Address Not Found: 99999", exception.getMessage());
	}
}
