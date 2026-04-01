package com.dev.minn.identityservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountStatus {

    INACTIVE(0), ACTIVE(1), BANNED(2), DELETED(3), PENDING(4);

    private final int status;

    public static AccountStatus fromStatus(Integer status) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.getStatus() == status) {
                return accountStatus;
            }
        }
        return null;
    }


    public static AccountStatus fromStatus(String status) {
        if (status == null)
            return null;

        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.name().equalsIgnoreCase(status)) {
                return accountStatus;
            }
        }
        return null;
    }
}