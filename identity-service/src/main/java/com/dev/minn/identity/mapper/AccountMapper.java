package com.dev.minn.identity.mapper;

import com.dev.minn.identity.dto.PendingAccountInfo;
import com.dev.minn.identity.dto.response.AccountDetail;
import com.dev.minn.identity.dto.response.AccountResponse;
import com.dev.minn.identity.entity.Account;
import com.dev.minn.identity.entity.associate.AccountRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "hashedPassword", target = "password")
    Account toEntity(PendingAccountInfo info);

    AccountResponse toResponse(Account account);

    AccountDetail toDetail(Account account);

    default List<String> mapRoles(Set<AccountRole> roles) {
        if (roles == null) return Collections.emptyList();

        return roles.stream()
                .map(ar -> ar.getRole().getName())
                .toList();
    }
}
