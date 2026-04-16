package com.dev.minn.order.grpc;

import com.bookstore.common.grpc.inventory.InventoryGrpcServiceGrpc;
import com.bookstore.common.grpc.inventory.ProductDetailsRequest;
import com.bookstore.common.grpc.inventory.ProductDetailsResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryClient {

    @GrpcClient("inventory-service")
    private InventoryGrpcServiceGrpc.InventoryGrpcServiceBlockingStub inventoryStub;

    public ProductDetailsResponse getProductDetails(List<String> productIds) {
        ProductDetailsRequest request = ProductDetailsRequest.newBuilder()
                .addAllProductIds(productIds)
                .build();
        return inventoryStub.getProductDetails(request);
    }
}
