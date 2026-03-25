package com.dev.minn.identityservice.converter;

import com.dev.minn.identityservice.constant.AccountStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AccountStatus accountStatus) {
        return accountStatus.getStatus();
    }

    @Override
    public AccountStatus convertToEntityAttribute(Integer status) {
        return AccountStatus.fromStatus(status);
    }
}
