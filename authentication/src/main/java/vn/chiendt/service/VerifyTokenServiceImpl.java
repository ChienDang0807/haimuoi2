package vn.chiendt.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.chiendt.dto.response.VerifyTokenResponse;

import vn.chiendt.grpc.VerifyTokenGrpcRequest;
import vn.chiendt.grpc.VerifyTokenGrpcResponse;
import vn.chiendt.grpc.VerifyTokenServiceGrpc;

@GrpcService
@RequiredArgsConstructor
@Slf4j(topic = "VERIFY-TOKEN-SERVICE")
public class VerifyTokenServiceImpl extends VerifyTokenServiceGrpc.VerifyTokenServiceImplBase{

    private final  AuthenticationService authenticationService;

    @Override
    public void verify(VerifyTokenGrpcRequest request, StreamObserver<VerifyTokenGrpcResponse> responseObserver) {
        log.info("Received request: {}...", request.getToken().substring(0, 15));

        VerifyTokenResponse response = authenticationService.getVerifyToken(request.getToken());

        VerifyTokenGrpcResponse grpcResponse = VerifyTokenGrpcResponse.newBuilder()
                .setStatus(response.getStatus())
                .setIsValid(response.isValid())
                .setMessage(response.getMessage())
                .setUsername(response.getUsername())
                .setUserId(response.getUserId() == null ? 0L : response.getUserId())
                .build();

        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }
}
