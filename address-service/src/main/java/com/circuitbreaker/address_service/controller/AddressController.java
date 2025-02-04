package com.circuitbreaker.address_service.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.circuitbreaker.address_service.model.Address;
import com.circuitbreaker.address_service.service.AddressService;

// @RestController
// @RequestMapping("addresses")
// public class AddressController {
//     @Autowired
//     private AddressService addressService;

//     @GetMapping("/{postalCode}")
//     public Address getAddressByPostalCode(@PathVariable("postalCode") String postalCode) {
//         return addressService.getAddressByPostalCode(postalCode);
//     }
@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/addresses/{postalCode}")
    public ResponseEntity<Address> getAddress(@PathVariable String postalCode) {
        Address address = addressService.getAddressByPostalCode(postalCode);
        System.out.println(address);
        return ResponseEntity.ok(address);
    }
}
