package vn.chiendt.service;

import vn.chiendt.dto.request.UserCreationRequest;
import vn.chiendt.dto.request.UserPasswordRequest;
import vn.chiendt.dto.request.UserUpdateRequest;
import vn.chiendt.dto.response.UserPageResponse;
import vn.chiendt.dto.response.UserResponse;

public interface UserService {
    UserPageResponse findAll(String keyword, String sort, int page, int size);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);
}
