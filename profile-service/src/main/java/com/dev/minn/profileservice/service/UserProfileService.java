package com.dev.minn.profileservice.service;

import com.dev.minn.profileservice.domain.UserProfile;
import com.dev.minn.profileservice.dto.request.UserProfileCreateRequest;
import com.dev.minn.profileservice.dto.response.UserProfileSummary;
import com.dev.minn.profileservice.mapper.UserProfileMapper;
import com.dev.minn.profileservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {

    UserProfileRepository userProfileRepository;

    UserProfileMapper userProfileMapper;

    @Transactional
    public UserProfileSummary createUserProfile(UserProfileCreateRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);

        return userProfileMapper.toSummary(userProfileRepository.save(userProfile));
    }
}
