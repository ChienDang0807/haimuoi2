package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.service.AddressService;

@RestController
@RequestMapping("/address")
@Tag(name = "Address-Controller")
@Slf4j(topic = "ADDRESS-CONTROLLER")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Get all addresses by user id", description = "Get all addresses by user id")
    @GetMapping("/list/{userId}")
    public ApiResponse getListAddress(@PathVariable Long userId) {
        log.info("Get address list");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Get all address by user id")
                .data(addressService.findAllAddressByUserId(userId))
                .build();
    }

    @Operation(summary = "Get address by id", description = "Get address by id")
    @GetMapping("/{addressId}")
    public ApiResponse getAddressDetail(@PathVariable Long addressId) {
        log.info("Get address ");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Get address by id")
                .data(addressService.findAddressById(addressId))
                .build();
    }

}
