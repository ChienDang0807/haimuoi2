package vn.chiendt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.chiendt.dto.response.AddressResponse;
import vn.chiendt.model.Address;
import vn.chiendt.repository.AddressRepository;
import vn.chiendt.service.AddressService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    public List<AddressResponse> findAllAddressByUserId(Long userId) {
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        return addresses.stream()
                .map(this::toAddressResponse)
                .toList();
    }

    @Override
    public AddressResponse findAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .userId(address.getUserId())
                .apartmentNumber(address.getApartmentNumber())
                .floor(address.getFloor())
                .building(address.getBuilding())
                .streetNumber(address.getStreetNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .country(address.getCountry())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
