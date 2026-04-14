package com.dev.minn.identity.client;

import com.dev.minn.grpc.profile.CreateProfileRequest;
import com.dev.minn.grpc.profile.CreateProfileResponse;
import com.dev.minn.grpc.profile.ProfileGrpcServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ProfileClient {

    @NonFinal
    @GrpcClient("profile-service")
    ProfileGrpcServiceGrpc.ProfileGrpcServiceBlockingStub profileStub;

    public CreateProfileResponse createProfile(CreateProfileRequest request) {
        return profileStub.createProfile(request);
    }
}
