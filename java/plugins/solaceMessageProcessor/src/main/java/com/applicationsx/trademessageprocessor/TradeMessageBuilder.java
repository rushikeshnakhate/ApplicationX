package com.applicationsx.trademessageprocessor;

import fixprocessor.TradeOuterClass;

public class TradeMessageBuilder {
    public static TradeOuterClass.Trade newOrder(String orderId, String symbol, int quantity, double price) {
        return TradeOuterClass.Trade.newBuilder()
                .setNewOrder(TradeOuterClass.NewOrder.newBuilder()
                        .setOrderId(orderId)
                        .setSymbol(symbol)
                        .setQuantity(quantity)
                        .setPrice(price)
                        .build())
                .build();
    }

    public static TradeOuterClass.Trade cancel(String orderId) {
        return TradeOuterClass.Trade.newBuilder()
                .setCancel(TradeOuterClass.Cancel.newBuilder()
                        .setOrderId(orderId)
                        .build())
                .build();
    }

    public static TradeOuterClass.Trade amend(String orderId, int newQuantity, double newPrice) {
        return TradeOuterClass.Trade.newBuilder()
                .setAmend(TradeOuterClass.Amend.newBuilder()
                        .setOrderId(orderId)
                        .setNewQuantity(newQuantity)
                        .setNewPrice(newPrice)
                        .build())
                .build();
    }
} 