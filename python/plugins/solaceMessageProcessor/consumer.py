import logging
from solace.messaging.messaging_service import MessagingService
from solace.messaging.resources.topic import Topic
from solace.messaging.resources.topic_subscription import TopicSubscription
from message_handler import MessageHandler

logger = logging.getLogger(__name__)


class Consumer:
    def __init__(self, messaging_service: MessagingService, topics, message_handler: MessageHandler):
        self.messaging_service = messaging_service
        # Handle both Topic objects and strings safely
        self.topic_subscriptions = []
        for topic in topics:
            if isinstance(topic, Topic):
                self.topic_subscriptions.append(TopicSubscription.of(topic.get_name()))
            elif isinstance(topic, str):
                self.topic_subscriptions.append(TopicSubscription.of(topic))
            else:
                raise ValueError(f"Invalid topic type: {type(topic)}. Expected Topic or str")

        self.message_handler = message_handler
        self.receiver = None

    def start(self):
        try:
            self.receiver = (self.messaging_service.create_direct_message_receiver_builder()
                             .with_subscriptions(self.topic_subscriptions)
                             .build())
            self.receiver.receive_async(self.message_handler)
            self.receiver.start()
            logger.info(f"Consumer started for topics: {[sub.get_name() for sub in self.topic_subscriptions]}")
        except Exception as e:
            logger.error(f"Failed to start consumer: {e}")
            raise

    def stop(self):
        if self.receiver:
            self.receiver.terminate()
            logger.info("Consumer stopped")
