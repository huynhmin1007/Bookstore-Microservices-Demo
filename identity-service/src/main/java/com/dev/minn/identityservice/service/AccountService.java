package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.constant.AccountStatus;
import com.dev.minn.identityservice.dto.request.AccountSearchRequest;
import com.dev.minn.identityservice.dto.response.AccountDetail;
import com.dev.minn.identityservice.dto.response.AccountSummary;
import com.dev.minn.identityservice.entity.Account;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.mapper.AccountMapper;
import com.dev.minn.identityservice.repository.AccountRepository;
import com.dev.minn.identityservice.specification.AccountSpecification;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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
        Account account = accountRepository
                .findDetail_ById(accountId)
                .orElseThrow(CodeException.USER_NOT_FOUND::throwException);

        if (account.getStatus() == AccountStatus.DELETED)
            throw CodeException.USER_NOT_FOUND.throwException();

        return accountMapper.toDetail(account);
    }

    public Page<AccountSummary> searchAccounts(AccountSearchRequest request) {
        Specification<Account> specification = AccountSpecification.buildSearchQuery(
                request.getKeyword(),
                AccountStatus.fromStatus(request.getStatus()),
                request.getRole()
        );

        Page<Account> page = accountRepository.findAll(specification, request.toPageable());

        return page.map(accountMapper::toSummary);
    }
}