package com.applicationsx.trademessageprocessor;

import fixprocessor.TradeOuterClass.Trade;
import fixprocessor.TradeOuterClass.NewOrder;
import fixprocessor.TradeOuterClass.Cancel;
import fixprocessor.TradeOuterClass.Amend;

public class MessageBuilder {

    public byte[] buildNewOrder(String orderId, String symbol, int quantity, double price) {
        NewOrder newOrder = NewOrder.newBuilder().setOrderId(orderId).setSymbol(symbol).setQuantity(quantity).setPrice(price).build();

        Trade trade = Trade.newBuilder().setNewOrder(newOrder).build();
        return trade.toByteArray();
    }

    public byte[] buildCancel(String orderId) {
        Cancel cancel = Cancel.newBuilder().setOrderId(orderId).build();
        Trade trade = Trade.newBuilder().setCancel(cancel).build();
        return trade.toByteArray();
    }

    public byte[] buildAmend(String orderId, int newQty, double newPrice) {
        Amend amend = Amend.newBuilder().setOrderId(orderId).setNewQuantity(newQty).setNewPrice(newPrice).build();

        Trade trade = Trade.newBuilder().setAmend(amend).build();
        return trade.toByteArray();
    }
}
