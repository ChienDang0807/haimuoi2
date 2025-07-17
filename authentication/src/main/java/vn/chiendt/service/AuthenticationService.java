package vn.chiendt.service;

import vn.chiendt.dto.request.SignInRequest;
import vn.chiendt.dto.response.TokenResponse;
import vn.chiendt.dto.response.VerifyTokenResponse;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(String request);

    VerifyTokenResponse getVerifyToken(String request);
}
