package com.applicationsx.trademessageprocessor;

import fixprocessorho.TradeOuterClass;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java Main produce|consume");
            return;
        }
        String host = "tcp://localhost:55555";
        String vpn = "default";
        String username = "admin";
        String password = "admin";
        String topic = "trades";
        if (args[0].equalsIgnoreCase("produce")) {
            TradeProducer producer = new TradeProducer(host, vpn, username, password, topic);
            TradeOuterClass.Trade trade = TradeMessageBuilder.newOrder("ORD123", "AAPL", 100, 150.5);
            producer.send(trade);
            System.out.println("Produced Trade: " + trade);
            producer.close();
        } else if (args[0].equalsIgnoreCase("consume")) {
            TradeConsumer consumer = new TradeConsumer(host, vpn, username, password, topic);
            System.out.println("Listening for trades. Press Ctrl+C to exit.");
            Thread.sleep(Long.MAX_VALUE);
            consumer.close();
        } else {
            System.out.println("Unknown mode: " + args[0]);
        }
    }
} 