package com.applicationsx.trademessageprocessor;

import com.solace.messaging.MessagingService;
import com.solace.messaging.resources.Topic;
import com.solace.messaging.resources.TopicSubscription;

import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        MessagingService messagingService = MessagingService.builder()
                .fromProperties(SolaceConfig.brokerProps)
                .build()
                .connect();

        List<Topic> topics = List.of(
                Topic.of(SolaceConfig.TOPICS.get("new_order")),
                Topic.of(SolaceConfig.TOPICS.get("cancel")),
                Topic.of(SolaceConfig.TOPICS.get("amend"))
        );

        List<TopicSubscription> subscriptions = topics.stream()
                .map(topic -> TopicSubscription.of(topic.getName()))
                .toList();

        MessageBuilder builder = new MessageBuilder();
        MessageHandlerImpl handler = new MessageHandlerImpl();
        Producer producer = new Producer(messagingService, topics, builder);
        Consumer consumer = new Consumer(messagingService, subscriptions, handler);

        consumer.start();
        producer.start();
        producer.publishMessages();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            producer.stop();
            consumer.stop();
            messagingService.disconnect();
        }));
    }
}