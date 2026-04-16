package com.dev.minn.common.messaging.contract.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySuccessEvent {

    String orderId;
    String status = "SUCCESS";
}
