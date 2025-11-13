package vn.chiendt.service;

import vn.chiendt.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    /**
     *  Find all adress by userId
     * @param userId
     * @return
     */
    List<AddressResponse> findAllAddressByUserId(Long userId);

    /**
     *  Find address by userId and addressName
     * @param
     * @return
     */
    AddressResponse findAddressById(Long id);
}
