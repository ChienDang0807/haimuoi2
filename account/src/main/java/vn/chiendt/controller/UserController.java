package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.request.UserCreationRequest;
import vn.chiendt.dto.request.UserPasswordRequest;
import vn.chiendt.dto.request.UserUpdateRequest;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user list", description = "API retrieve user from database")
    @GetMapping("/list")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        log.info("Get user list");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("users")
                .data(userService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by ID from database")
    @GetMapping("/{userId}")
    public ApiResponse getUserDetail(@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId) {
        log.info("Get user detail by ID: {}", userId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userService.findById(userId))
                .build();
    }

    @Operation(summary = "Create User", description = "API add new user to database")
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create User: {}", request);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(userService.save(request))
                .build();
    }

    @Operation(summary = "Update User", description = "API update user to database")
    @PutMapping("/upd")
    public ApiResponse updateUser(@RequestBody @Valid UserUpdateRequest request) {
        log.info("Updating user: {}", request);

        userService.update(request);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("User updated successfully")
                .data("")
                .build();
    }

    @Operation(summary = "Change Password", description = "API change password for user to database")
    @PatchMapping("/change-pwd")
    public ApiResponse changePassword(@RequestBody @Valid UserPasswordRequest request) {
        log.info("Changing password for user: {}", request);

        userService.changePassword(request);

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Password updated successfully")
                .data("")
                .build();
    }

    @Operation(summary = "Confirm Email", description = "Confirm email for account")
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirm email for account with secretCode: {}", secretCode);

        try {
            // check or compare secret code from db
        } catch (Exception e) {
            log.error("Verification fail, message={}", e.getMessage(), e);
        } finally {
            // direct to login page
            response.sendRedirect("https://tayjava.vn/wp-admin/");
        }
    }

    @Operation(summary = "Delete user", description = "API activate user from database")
    @DeleteMapping("/del/{userId}")
    public ApiResponse deleteUser(@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId) {
        log.info("Deleting user: {}", userId);

        userService.delete(userId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("User deleted successfully")
                .data("")
                .build();
    }
}