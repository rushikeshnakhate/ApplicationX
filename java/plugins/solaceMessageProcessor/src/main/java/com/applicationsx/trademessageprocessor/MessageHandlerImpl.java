package com.applicationsx.trademessageprocessor;

import com.solace.messaging.receiver.InboundMessage;
import com.solace.messaging.receiver.MessageReceiver;
import fixprocessor.TradeOuterClass.Trade;

import java.util.logging.Logger;

public class MessageHandlerImpl implements MessageReceiver.MessageHandler {
    private static final Logger logger = Logger.getLogger(MessageHandlerImpl.class.getName());

    @Override
    public void onMessage(InboundMessage message) {
        try {
            byte[] payload = message.getPayloadAsBytes();
            Trade trade = Trade.parseFrom(payload);
            String topic = message.getDestinationName();

            if (trade.hasNewOrder()) {
                var order = trade.getNewOrder();
                logger.info("NewOrder on " + topic + " => " + order);
            } else if (trade.hasCancel()) {
                logger.info("Cancel on " + topic + " => " + trade.getCancel());
            } else if (trade.hasAmend()) {
                logger.info("Amend on " + topic + " => " + trade.getAmend());
            }
        } catch (Exception e) {
            logger.severe("Error parsing message: " + e.getMessage());
        }
    }
} 