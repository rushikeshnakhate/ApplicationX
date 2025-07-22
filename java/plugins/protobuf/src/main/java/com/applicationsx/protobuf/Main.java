package com.applicationsx.protobuf;

import java.time.Instant;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Java Protobuf Order Conversion ===");

        // Create sample order
        Order order = new Order("ORD-12345", "John Smith", Arrays.asList("Laptop", "Mouse", "Keyboard"), 1299.99, "PENDING", Instant.now());

        System.out.println("Original Order: " + order);

        // Convert to protobuf
        OrderProto.OrderMessage protoMsg = OrderConverter.toProto(order);
        byte[] data = protoMsg.toByteArray();
        System.out.println("Serialized size: " + data.length + " bytes");

        // Deserialize back
        try {
            OrderProto.OrderMessage parsed = OrderProto.OrderMessage.parseFrom(data);
            Order restored = OrderConverter.fromProto(parsed);
            System.out.println("Restored Order: " + restored);

            boolean isValid = order.getId().equals(restored.getId()) && order.getCustomerName().equals(restored.getCustomerName()) && order.getItems().equals(restored.getItems()) && order.getTotalAmount() == restored.getTotalAmount() && order.getStatus().equals(restored.getStatus());

            System.out.println("Integrity Check: " + (isValid ? "✓ PASS" : "✗ FAIL"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
