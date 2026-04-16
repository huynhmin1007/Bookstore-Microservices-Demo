package com.dev.minn.common.messaging.contract.command;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class CreatePaymentCommand {

    String orderId;
    String userId;
    String paymentMethod;
    double amount;
}
