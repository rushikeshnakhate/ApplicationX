package com.applicationsx.trademessageprocessor;

import com.solace.messaging.MessagingService;
import com.solace.messaging.receiver.DirectMessageReceiver;

import com.solace.messaging.receiver.MessageReceiver;
import com.solace.messaging.resources.TopicSubscription;

import java.util.List;

public class Consumer {
    private final MessagingService messagingService;
    private final List<TopicSubscription> topicSubscriptions;
    private final MessageReceiver.MessageHandler handler;
    private DirectMessageReceiver receiver;

    public Consumer(MessagingService messagingService, List<TopicSubscription> subscriptions, MessageReceiver.MessageHandler handler) {
        this.messagingService = messagingService;
        this.topicSubscriptions = subscriptions;
        this.handler = handler;
    }

    public void start() {
        receiver = messagingService.createDirectMessageReceiverBuilder()
                .withSubscriptions(topicSubscriptions.toArray(new TopicSubscription[0]))
                .build()
                .start();
        receiver.receiveAsync(handler);
    }

    public void stop() {
        if (receiver != null) receiver.terminate(1000);
    }
}
