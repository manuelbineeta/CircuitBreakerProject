package com.circuitbreaker.address_service.service;

import com.circuitbreaker.address_service.model.Address;

public interface AddressService {
    Address getAddressByPostalCode(String postalCode);
}