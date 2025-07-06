package com.applicationsx.trademessageprocessor;

import com.solace.messaging.MessagingService;
import com.solace.messaging.publisher.DirectMessagePublisher;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.publisher.OutboundMessageBuilder;
import com.solace.messaging.resources.Topic;
//import com.solace.messaging.message.OutboundMessage;
//import com.solace.messaging.message.OutboundMessageBuilder;

import java.util.List;
import java.util.logging.Logger;

public class Producer {
    private final MessagingService messagingService;
    private final List<Topic> topics;
    private final MessageBuilder messageBuilder;
    private DirectMessagePublisher publisher;
    private static final Logger logger = Logger.getLogger(Producer.class.getName());

    public Producer(MessagingService messagingService, List<Topic> topics, MessageBuilder builder) {
        this.messagingService = messagingService;
        this.topics = topics;
        this.messageBuilder = builder;
    }

    public void start() {
        publisher = messagingService.createDirectMessagePublisherBuilder().build().start();
    }

    public void publishMessages() {
        OutboundMessageBuilder solaceBuilder = messagingService.messageBuilder();

        byte[] newOrderPayload = messageBuilder.buildNewOrder("ORD001", "AAPL", 100, 150.5);
        OutboundMessage msg1 = solaceBuilder.build(newOrderPayload);
        publisher.publish(msg1, topics.get(0));

        byte[] cancelPayload = messageBuilder.buildCancel("ORD001");
        publisher.publish(solaceBuilder.build(cancelPayload), topics.get(1));

        byte[] amendPayload = messageBuilder.buildAmend("ORD001", 200, 155.75);
        publisher.publish(solaceBuilder.build(amendPayload), topics.get(2));
    }

    public void stop() {
        if (publisher != null) publisher.terminate(1000);
    }
}
