import logging
from solace.messaging.messaging_service import MessagingService
from solace.messaging.resources.topic import Topic
from message_builder import MessageBuilder # Make sure this is correctly imported

logger = logging.getLogger(__name__)


class Producer:
    def __init__(self, messaging_service: MessagingService, topics: list[Topic], message_builder: MessageBuilder):
        self.messaging_service = messaging_service
        self.topics = topics
        self.message_builder = message_builder
        self.publisher = None

    def start(self):
        try:
            self.publisher = self.messaging_service.create_direct_message_publisher_builder().build()
            self.publisher.start()
            logger.info(f"Producer started for topics {[topic.get_name() for topic in self.topics]}")
        except Exception as e:
            logger.error(f"Failed to start publisher: {e}")
            raise

    def publish_messages(self):
        try:
            solace_message_builder = self.messaging_service.message_builder()

            # --- Publish NewOrder ---
            payload_bytes = self.message_builder.build_new_order("ORD001", "AAPL", 100, 150.5)
            # Convert bytes to bytearray to satisfy the IDE's type hint
            message = solace_message_builder.build(bytearray(payload_bytes))
            self.publisher.publish(message, self.topics[0])  # trade/new_order
            logger.info(f"Published message to topic {self.topics[0].get_name()}")

            # --- Publish Cancel ---
            payload_bytes = self.message_builder.build_cancel("ORD001")
            # Convert bytes to bytearray
            message = solace_message_builder.build(bytearray(payload_bytes))
            self.publisher.publish(message, self.topics[1])  # trade/cancel
            logger.info(f"Published message to topic {self.topics[1].get_name()}")

            # --- Publish Amend ---
            payload_bytes = self.message_builder.build_amend("ORD001", 200, 155.75)
            # Convert bytes to bytearray
            message = solace_message_builder.build(bytearray(payload_bytes))
            self.publisher.publish(message, self.topics[2])  # trade/amend
            logger.info(f"Published message to topic {self.topics[2].get_name()}")

        except Exception as e:
            logger.error(f"Failed to publish messages: {e}")
            raise

    def stop(self):
        if self.publisher:
            self.publisher.terminate()
            logger.info(f"Producer stopped for topics {[topic.get_name() for topic in self.topics]}")