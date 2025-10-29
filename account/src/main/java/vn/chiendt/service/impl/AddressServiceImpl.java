package vn.chiendt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.chiendt.model.Address;
import vn.chiendt.repository.AddressRepository;
import vn.chiendt.service.AddressService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    public List<Address> findAllAddressByUserId(Long userId) {
        return addressRepository.findAllByUserId(userId);
    }

    @Override
    public Address findAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }
}
