package com.dev.minn.order.mapper;

import com.dev.minn.common.messaging.contract.command.ReserveInventoryCommand;
import com.dev.minn.order.dto.OrderItemDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    ReserveInventoryCommand.Item toItem(OrderItemDto item);
    List<ReserveInventoryCommand.Item> toItems(List<OrderItemDto> items);
}
