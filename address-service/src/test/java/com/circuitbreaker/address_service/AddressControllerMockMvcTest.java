package com.circuitbreaker.address_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.circuitbreaker.address_service.model.Address;
import com.circuitbreaker.address_service.service.AddressService;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerMockMvcTest {

    // We can test Spring Boot REST Controller without starting an actual web
    // server. It simulates HTTP requests and verifies responses
    // efficiently..Focuses on Controller Layer. Test controller without actually
    // running the application on localhost:8080, and MockMvc simulates HTTP
    // requests within the Spring test environment

    // Step 1 .JUnit runs the test method.
    // step 2.mockMvc.perform(get("/addresses/10001")) simulates an HTTP request as
    // if it were coming from an actual client .
    // Step 3. Request Reaches the Controller
    // Step 4 The request flows through controller → service → response.MockMvc then
    // asserts the HTTP status, content type, and JSON structure.
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Test
    void testGetAddressByPostalCode_Found() throws Exception {

        Address mockAddress = new Address(1, "10001", "New York", "NY");
        when(addressService.getAddressByPostalCode("10001")).thenReturn(mockAddress);

        mockMvc.perform(get("/addresses/10001")) // Simulate GET request
                .andExpect(status().isOk()) // Expect 200 OK status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postalCode").value("10001"))
                .andExpect(jsonPath("$.city").value("NY"));
    }

    @Test
    void testGetAddressByPostalCode_NotFound() throws Exception {

        when(addressService.getAddressByPostalCode("99999"))
                .thenThrow(new RuntimeException("Address Not Found: 99999"));

        mockMvc.perform(get("/addresses/99999")) // Simulate GET request
                .andExpect(status().isNotFound())
                .andExpect(content().string("Address Not Found: 99999"));
    }
}
