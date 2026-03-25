package com.dev.minn.identityservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.yaml.snakeyaml.nodes.Tag.STR;

@AllArgsConstructor
@Getter
public enum AccountStatus {

    INACTIVE(0), ACTIVE(1), BANNED(2);

    private final int status;

    public static AccountStatus fromStatus(Integer status) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.getStatus() == status) {
                return accountStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }
}