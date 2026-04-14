package com.dev.minn.profile.mapper;

import com.dev.minn.grpc.profile.CreateProfileRequest;
import com.dev.minn.grpc.profile.CreateProfileResponse;
import com.dev.minn.profile.domain.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    UserProfile toProfile(CreateProfileRequest request);

    @Mapping(target = "profileId", source = "id")
    CreateProfileResponse toResponse(UserProfile profile);
}
