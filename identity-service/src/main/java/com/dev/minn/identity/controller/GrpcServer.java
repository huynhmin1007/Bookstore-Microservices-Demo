package com.dev.minn.identity.controller;

import com.dev.minn.common.exception.CodeException;
import com.dev.minn.grpc.identity.AccountResponse;
import com.dev.minn.grpc.identity.GetAccountRequest;
import com.dev.minn.grpc.identity.IdentityGrpcServiceGrpc;
import com.dev.minn.identity.entity.Account;
import com.dev.minn.identity.repository.AccountRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrpcServer extends IdentityGrpcServiceGrpc.IdentityGrpcServiceImplBase {

    AccountRepository accountRepository;

    @Override
    public void getAccount(GetAccountRequest request, StreamObserver<AccountResponse> responseObserver) {
        try {
            Account account = accountRepository.findById(UUID.fromString(request.getAccountId()))
                    .orElseThrow(CodeException.USER_NOT_FOUND::throwException);

            AccountResponse response = AccountResponse.newBuilder()
                    .setId(account.getId().toString())
                    .setEmail(account.getEmail())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }
}
