package com.dev.minn.inventory.service;

import com.dev.minn.common.exception.CodeException;
import com.dev.minn.common.messaging.contract.command.ReserveInventoryCommand;
import com.dev.minn.inventory.entity.InventoryItem;
import com.dev.minn.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class InventoryService {

    InventoryRepository inventoryRepository;

    @Transactional
    public void reserveStock(List<ReserveInventoryCommand.Item> items) {
        for(ReserveInventoryCommand.Item itemDto : items) {
            InventoryItem item = inventoryRepository.findByProductId(itemDto.getProductId())
                    .orElseThrow(CodeException.BOOK_NOT_FOUND::throwException);

            item.reserve(itemDto.getQuantity());
        }
    }

    @Transactional
    public void releaseStock(List<ReserveInventoryCommand.Item> items) {
        for(ReserveInventoryCommand.Item itemDto : items) {
            InventoryItem item = inventoryRepository.findByProductId(itemDto.getProductId())
                    .orElseThrow(CodeException.BOOK_NOT_FOUND::throwException);

            item.release(itemDto.getQuantity());
        }
    }

    @Transactional
    public void confirmDeductStock(List<ReserveInventoryCommand.Item> items) {
        for(ReserveInventoryCommand.Item itemDto : items) {
            InventoryItem item = inventoryRepository.findByProductId(itemDto.getProductId())
                    .orElseThrow(CodeException.BOOK_NOT_FOUND::throwException);
            item.confirmDeduct(itemDto.getQuantity());
        }
    }

    @Transactional
    public void addItemToInventory(String productId, int quantity, double price) {
        InventoryItem item = InventoryItem.builder()
                .productId(productId)
                .totalQuantity(quantity)
                .reservedQuantity(0)
                .price(new BigDecimal(price))
                .build();
        inventoryRepository.save(item);
    }
}
