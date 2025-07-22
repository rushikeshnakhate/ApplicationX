package com.applicationsx.protobuf;

import java.time.Instant;
import java.util.List;

public class Order {
    private String id;
    private String customerName;
    private List<String> items;
    private double totalAmount;
    private String status;
    private Instant createdAt;

    public Order(String id, String customerName, List<String> items, double totalAmount, String status, Instant createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters omitted for brevity

    @Override
    public String toString() {
        return "Order{" + "id='" + id + '\'' + ", customerName='" + customerName + '\'' + ", items=" + items.size() + ", totalAmount=" + totalAmount + ", status='" + status + '\'' + '}';
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<String> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
