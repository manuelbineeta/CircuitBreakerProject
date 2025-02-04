package com.circuitbreaker.address_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.circuitbreaker.address_service.model.Address;
import com.circuitbreaker.address_service.repository.AddressRepository;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public Address getAddressByPostalCode(String postalCode) {
        return addressRepository.findByPostalCode(postalCode)
                .orElseThrow(() -> new RuntimeException("Address Not Found: " + postalCode));
    }
}
