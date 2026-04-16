package com.dev.minn.common.messaging.contract.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PaymentSuccessfulEvent {
    String orderId;
    String paymentId;
    String status = "SUCCESS";
}
