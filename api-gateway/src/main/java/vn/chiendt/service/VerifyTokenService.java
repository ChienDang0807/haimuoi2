package vn.chiendt.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import vn.chiendt.grpc.VerifyTokenGrpcRequest;
import vn.chiendt.grpc.VerifyTokenGrpcResponse;
import vn.chiendt.grpc.VerifyTokenServiceGrpc;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@Slf4j(topic = "VERIFY-TOKEN-SERVICE")
public class VerifyTokenService {

    @GrpcClient("verify-token-service")
    private VerifyTokenServiceGrpc.VerifyTokenServiceBlockingStub blockingStub;

    public VerifyTokenGrpcResponse verifyAccessToken(String token) {
        log.info("verifyAccessToken called");

        // Create request
        VerifyTokenGrpcRequest request = VerifyTokenGrpcRequest.newBuilder().setToken(token).build();
        try {
            // Send request via gRPC
            return blockingStub.verify(request);
        } catch (Exception e) {
            log.error("Call auth-service fail, message: {}", e.getMessage(), e);
            return VerifyTokenGrpcResponse.newBuilder()
                    .setStatus(FORBIDDEN.value())
                    .setMessage(e.getMessage())
                    .setIsValid(false)
                    .setUsername("")
                    .build();
        }

    }
}
