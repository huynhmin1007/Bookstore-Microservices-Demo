package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.dto.PendingAccountInfo;
import com.dev.minn.identityservice.dto.request.RegistrationInitRequest;
import com.dev.minn.identityservice.dto.request.RegistrationVerifyRequest;
import com.dev.minn.identityservice.dto.response.AccountSummary;
import com.dev.minn.identityservice.entity.Account;
import com.dev.minn.identityservice.event.SendRegistrationOtpEmailEvent;
import com.dev.minn.identityservice.event.SendWelcomeEmailEvent;
import com.dev.minn.identityservice.exception.AppException;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.mapper.AccountMapper;
import com.dev.minn.identityservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class AccountService {

    AccountRepository accountRepository;
    PasswordEncoder passwordEncoder;

}