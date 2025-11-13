package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.response.AddressResponse;
import vn.chiendt.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/address")
@Tag(name = "Address-Controller")
@Slf4j(topic = "ADDRESS-CONTROLLER")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Get all addresses by user id", description = "Get all addresses by user id")
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<AddressResponse>> getListAddress(@PathVariable Long userId) {
        log.info("Get address list");

        return ResponseEntity.ok(addressService.findAllAddressByUserId(userId));
    }

    @Operation(summary = "Get address by id", description = "Get address by id")
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressDetail(@PathVariable Long addressId) {
        log.info("Get address ");

        return ResponseEntity.ok(addressService.findAddressById(addressId));
    }

}
