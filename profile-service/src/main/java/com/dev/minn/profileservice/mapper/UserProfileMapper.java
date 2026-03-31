package com.dev.minn.profileservice.mapper;

import com.dev.minn.profileservice.domain.UserProfile;
import com.dev.minn.profileservice.dto.request.UserProfileCreateRequest;
import com.dev.minn.profileservice.dto.response.UserProfileSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfile toUserProfile(UserProfileCreateRequest request);
    UserProfileSummary toSummary(UserProfile userProfile);
}
