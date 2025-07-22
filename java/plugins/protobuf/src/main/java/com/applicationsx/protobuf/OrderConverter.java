package com.applicationsx.protobuf;


// This imports the outer wrapper class generated from the .proto


import java.time.Instant;

public class OrderConverter {

    public static Order fromProto(OrderProto.OrderMessage message) {
        return new Order(
                message.getId(),
                message.getCustomerName(),
                message.getItemsList(),
                message.getTotalAmount(),
                message.getStatus(),
                Instant.ofEpochSecond(message.getCreatedTimestamp())
        );
    }

    public static OrderProto.OrderMessage toProto(Order order) {
        return OrderProto.OrderMessage.newBuilder()
                .setId(order.getId())
                .setCustomerName(order.getCustomerName())
                .addAllItems(order.getItems())
                .setTotalAmount(order.getTotalAmount())
                .setStatus(order.getStatus())
                .setCreatedTimestamp(order.getCreatedAt().getEpochSecond())
                .build();
    }
}
