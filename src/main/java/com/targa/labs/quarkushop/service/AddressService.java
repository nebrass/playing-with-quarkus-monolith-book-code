package com.targa.labs.quarkushop.service;

import com.targa.labs.quarkushop.domain.Address;
import com.targa.labs.quarkushop.web.dto.AddressDto;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AddressService {

    public static Address createFromDto(AddressDto addressDto) {
        return new Address(
                addressDto.getAddress1(),
                addressDto.getAddress2(),
                addressDto.getCity(),
                addressDto.getPostcode(),
                addressDto.getCountry()
        );
    }

    public static AddressDto mapToDto(Address address) {
        return new AddressDto(
                address.getAddress1(),
                address.getAddress2(),
                address.getCity(),
                address.getPostcode(),
                address.getCountry()
        );

    }
}
