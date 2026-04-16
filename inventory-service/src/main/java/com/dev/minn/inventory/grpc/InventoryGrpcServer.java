package com.dev.minn.inventory.grpc;

import com.bookstore.common.grpc.inventory.InventoryGrpcServiceGrpc;
import com.bookstore.common.grpc.inventory.ProductDetailsRequest;
import com.bookstore.common.grpc.inventory.ProductDetailsResponse;
import com.bookstore.common.grpc.inventory.ProductItem;
import com.dev.minn.inventory.entity.InventoryItem;
import com.dev.minn.inventory.repository.InventoryRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InventoryGrpcServer extends InventoryGrpcServiceGrpc.InventoryGrpcServiceImplBase {

    InventoryRepository inventoryRepository;

    @Override
    public void getProductDetails(
            ProductDetailsRequest request,
            StreamObserver<ProductDetailsResponse> responseObserver
    ) {
        List<String> productIds = request.getProductIdsList();

       List<InventoryItem> items = inventoryRepository.findAllByProductIdIn(productIds);

       ProductDetailsResponse.Builder responseBuilder = ProductDetailsResponse.newBuilder();

        for (InventoryItem item : items) {
            ProductItem grpcItem = ProductItem.newBuilder()
                    .setProductId(item.getProductId())
                    .setPrice(item.getPrice().toString())
                    .setAvailableQuantity(item.getTotalQuantity() - item.getReservedQuantity())
                    .setIsStockAvailable((item.getTotalQuantity() - item.getReservedQuantity()) > 0)
                    .build();
            responseBuilder.addItems(grpcItem);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
