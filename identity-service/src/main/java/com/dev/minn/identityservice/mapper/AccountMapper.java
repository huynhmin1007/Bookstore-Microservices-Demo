package com.dev.minn.identityservice.mapper;

import com.dev.minn.identityservice.dto.PendingAccountInfo;
import com.dev.minn.identityservice.dto.response.AccountSummary;
import com.dev.minn.identityservice.entity.Account;
import com.dev.minn.identityservice.entity.Role;
import com.dev.minn.identityservice.entity.associate.AccountRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "hashedPassword", target = "password")
    Account toEntity(PendingAccountInfo info);

    AccountSummary toSummary(Account account);

    default List<String> mapRoles(Set<AccountRole> roles) {
        if (roles == null) return Collections.emptyList();

        return roles.stream()
                .map(ar -> ar.getRole().getName())
                .toList();
    }
}
