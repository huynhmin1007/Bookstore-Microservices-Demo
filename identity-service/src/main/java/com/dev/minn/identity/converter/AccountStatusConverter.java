package com.dev.minn.identity.converter;

import com.dev.minn.identity.constant.AccountStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AccountStatus accountStatus) {
        return accountStatus.getStatus();
    }

    @Override
    public AccountStatus convertToEntityAttribute(Integer value) {
        AccountStatus status = AccountStatus.fromStatus(value);
        if(status == null)
            throw new IllegalArgumentException("Invalid status value: " + value);

        return status;
    }
}
