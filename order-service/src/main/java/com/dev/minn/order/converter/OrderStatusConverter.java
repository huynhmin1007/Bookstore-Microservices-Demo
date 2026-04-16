package com.dev.minn.order.converter;

import com.dev.minn.order.constant.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderStatus orderStatus) {
        return orderStatus.getStatus();
    }

    @Override
    public OrderStatus convertToEntityAttribute(Integer integer) {
       OrderStatus status = OrderStatus.fromStatus(integer);

       if(status == null)
           throw new IllegalArgumentException("Invalid status value: " + integer);

       return status;
    }
}
