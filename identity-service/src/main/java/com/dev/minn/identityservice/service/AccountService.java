package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.dto.response.AccountDetail;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.mapper.AccountMapper;
import com.dev.minn.identityservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class AccountService {

    AccountRepository accountRepository;

    PasswordEncoder passwordEncoder;

    AccountMapper accountMapper;

    public AccountDetail getDetailAccount(UUID accountId) {
        return accountMapper.toDetail(
                accountRepository
                        .findDetail_ById(accountId)
                        .orElseThrow(CodeException.USER_NOT_FOUND::throwException)
        );
    }
}