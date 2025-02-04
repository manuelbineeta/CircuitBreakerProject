package com.circuitbreaker.address_service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import com.circuitbreaker.address_service.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Starts server at random port
class AddressControllerTestRestTemplateTest {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    // This approach tests the full Spring Boot flow, including controller, service,
    // and repository layers. Uses real HTTP requests. Ensures everything
    // (Controller → Service → Repository → Database) works.Best for: End-to-End
    // (E2E) Testing
    @Autowired
    private TestRestTemplate testRestTemplate; // Injects TestRestTemplate

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/addresses/";
    }

    @Test
    void testGetAddressByPostalCode_Found() {
        // Perform GET request to controller endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<Address> response = testRestTemplate.exchange("http://localhost:9090/addresses/1000001",
                HttpMethod.GET, entity, new ParameterizedTypeReference<Address>() {
                });
        // ResponseEntity<Address> response = restTemplate.getForEntity(baseUrl +
        // "10001", Address.class);

        // Verify the response status and content
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPostalCode()).isEqualTo("1000001");
    }

    @Test
    void testGetAddressByPostalCode_NotFound() {
        // Perform GET request for a non-existing postal code
        ResponseEntity<String> response = testRestTemplate.getForEntity(baseUrl + "99999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Address Not Found: 99999");
    }
}
