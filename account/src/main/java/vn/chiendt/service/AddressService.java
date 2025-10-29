package vn.chiendt.service;

import vn.chiendt.model.Address;

import java.util.List;

public interface AddressService {
    /**
     *  Find all adress by userId
     * @param userId
     * @return
     */
    List<Address> findAllAddressByUserId(Long userId);

    /**
     *  Find address by userId and addressName
     * @param
     * @return
     */
    Address findAddressById(Long id);
}
