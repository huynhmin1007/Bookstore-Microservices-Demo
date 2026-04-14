package com.dev.minn.profile.controller;

import com.dev.minn.common.exception.CodeException;
import com.dev.minn.grpc.profile.*;
import com.dev.minn.profile.domain.UserProfile;
import com.dev.minn.profile.mapper.ProfileMapper;
import com.dev.minn.profile.repository.UserProfileRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileGrpcController extends ProfileGrpcServiceGrpc.ProfileGrpcServiceImplBase {

    ProfileMapper profileMapper;
    UserProfileRepository profileRepository;

    @Override
    public void createProfile(CreateProfileRequest request, StreamObserver<CreateProfileResponse> responseObserver) {
        log.info("Received internal gRPC request to create profile for accountId: {}", request.getAccountId());

        try {
            if (!StringUtils.hasText(request.getAccountId()) || !StringUtils.hasText(request.getEmail())) {
                log.warn("Validation failed for gRPC request");
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("AccountId and Email must be not empty")
                        .asRuntimeException());
                return;
            }

            CreateProfileResponse response = create(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error occurred while creating profile", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed while creating profile")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getProfile(GetProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        try {
            UserProfile profile = profileRepository.findByAccountId(request.getAccountId())
                    .orElseThrow(CodeException.USER_NOT_FOUND::throwException);

            ProfileResponse response = ProfileResponse.newBuilder()
                    .setAccountId(profile.getAccountId())
                    .setProfileId(profile.getId().toString())
                    .setEmail(profile.getEmail())
                    .setFirstName(profile.getFirstName())
                    .setLastName(profile.getLastName())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error occurred while getting profile", e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Failed while getting profile")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    private CreateProfileResponse create(CreateProfileRequest request) {
        UserProfile profile = profileMapper.toProfile(request);

        return profileMapper.toResponse(profileRepository.save(profile));
    }
}
