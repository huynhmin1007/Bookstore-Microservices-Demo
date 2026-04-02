package com.dev.minn.profileservice.controller; // Hoặc package grpc riêng

import com.dev.minn.grpc.profile.CreateProfileRequest;
import com.dev.minn.grpc.profile.CreateProfileResponse;
import com.dev.minn.grpc.profile.ProfileServiceGrpcGrpc;
import com.dev.minn.profileservice.dto.request.UserProfileCreateRequest;
import com.dev.minn.profileservice.service.UserProfileService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProfileGrpcController extends ProfileServiceGrpcGrpc.ProfileServiceGrpcImplBase {

    private final UserProfileService userProfileService;

    @Override
    public void createProfile(CreateProfileRequest request, StreamObserver<CreateProfileResponse> responseObserver) {
        log.info("Nhận được request gRPC tạo profile cho account: {}", request.getAccountId());

        try {
            UserProfileCreateRequest dto = new UserProfileCreateRequest();
            dto.setUserId(request.getAccountId());
            dto.setFirstName(request.getFirstName());
            dto.setLastName(request.getLastName());

            userProfileService.createUserProfile(dto);

            CreateProfileResponse response = CreateProfileResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Lỗi khi xử lý gRPC: ", e);
            // 4. Trả lỗi về cho Identity Service
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Lỗi hệ thống: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}