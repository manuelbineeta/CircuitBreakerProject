package com.circuitbreaker.address_service.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.circuitbreaker.address_service.model.Address;
import com.circuitbreaker.address_service.repository.AddressRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class DataSetup {
    @Autowired
    private AddressRepository addressRepository;

    @PostConstruct
    public void setupData() {
        addressRepository.saveAll(Arrays.asList(
                Address.builder().postalCode("1000001").state("Tokyo").city("Chiyoda")
                        .build(),
                Address.builder().postalCode("1100000").state("Tokyo").city("Taito").build(),
                Address.builder().postalCode("2100001").state("Kanagawa").city("Kawasaki")
                        .build()));
    }
}
