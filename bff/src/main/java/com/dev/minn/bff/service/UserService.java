package com.dev.minn.bff.service;

import com.dev.minn.bff.dto.response.UserDetailResponse;
import com.dev.minn.grpc.identity.AccountResponse;
import com.dev.minn.grpc.identity.GetAccountRequest;
import com.dev.minn.grpc.identity.IdentityGrpcServiceGrpc;
import com.dev.minn.grpc.profile.GetProfileRequest;
import com.dev.minn.grpc.profile.ProfileGrpcServiceGrpc;
import com.dev.minn.grpc.profile.ProfileResponse;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @GrpcClient("identity-service")
    private IdentityGrpcServiceGrpc.IdentityGrpcServiceFutureStub identityStub;

    @GrpcClient("profile-service")
    private ProfileGrpcServiceGrpc.ProfileGrpcServiceFutureStub profileStub;

    public UserDetailResponse getUserDetail(String accountId) {
        GetAccountRequest accountReq = GetAccountRequest.newBuilder()
                .setAccountId(accountId)
                .build();

        GetProfileRequest profileReq = GetProfileRequest.newBuilder()
                .setAccountId(accountId)
                .build();

        ListenableFuture<AccountResponse> accountFuture = identityStub.getAccount(accountReq);
        ListenableFuture<ProfileResponse> profileFuture = profileStub.getProfile(profileReq);

        try {
            AccountResponse accountData = accountFuture.get();
            ProfileResponse profileData = profileFuture.get();

            return UserDetailResponse.builder()
                    .accountId(accountData.getId())
                    .profileId(profileData.getProfileId())
                    .email(accountData.getEmail())
                    .firstName(profileData.getFirstName())
                    .lastName(profileData.getLastName())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user data", e);
        }
    }
}
