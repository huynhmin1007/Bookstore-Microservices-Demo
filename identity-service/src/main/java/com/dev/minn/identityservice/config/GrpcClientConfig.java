package com.dev.minn.identityservice.config;

import com.dev.minn.grpc.profile.ProfileServiceGrpcGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ProfileServiceGrpcGrpc.ProfileServiceGrpcBlockingStub profileGrpcStub(GrpcChannelFactory channelFactory) {
        return ProfileServiceGrpcGrpc.newBlockingStub(
                channelFactory.createChannel("profile-server")
        );
    }
}
