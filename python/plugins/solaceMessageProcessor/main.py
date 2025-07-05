import logging
from solace.messaging.messaging_service import MessagingService
from solace.messaging.resources.topic import Topic
from solace_config import SolaceConfig
from consumer import Consumer
from producer import Producer
from message_handler import MessageHandler
from message_builder import MessageBuilder
import time

logger = logging.getLogger(__name__)


def main():
    # Initialize variables to None to handle cleanup properly
    messaging_service = None
    consumer = None
    producer = None

    try:
        config = SolaceConfig()
        logger.info("Loaded configuration ...")

        # Build messaging service with retry logic
        messaging_service = MessagingService.builder().from_properties(config.broker_props).build()
        logger.info("Messaging service created ...")

        # Connect with retry
        for attempt in range(3):
            try:
                messaging_service.connect()
                logger.info("Connected to Solace event broker")
                break
            except Exception as e:
                if attempt == 2:
                    raise
                logger.warning(f"Connection attempt {attempt + 1} failed, retrying...")
                time.sleep(2)

        # Create topic subscriptions
        topics = [
            Topic.of(config.topics["new_order"]),
            Topic.of(config.topics["cancel"]),
            Topic.of(config.topics["amend"])
        ]

        message_handler = MessageHandler()
        message_builder = MessageBuilder()

        # Initialize consumer and producer
        consumer = Consumer(messaging_service, topics, message_handler)
        producer = Producer(messaging_service, topics, message_builder)

        # Start services
        consumer.start()
        producer.start()

        # Publish initial messages
        producer.publish_messages()

        # Keep the application running to receive messages
        logger.info("Application running, press Ctrl+C to stop...")
        while True:
            time.sleep(1)

    except KeyboardInterrupt:
        logger.info("Shutting down gracefully...")
    except Exception as e:
        logger.error(f"Error running application: {e}")
        raise
    finally:
        # Clean up resources in reverse order
        try:
            if producer:
                producer.stop()
        except Exception as e:
            logger.error(f"Error stopping producer: {e}")

        try:
            if consumer:
                consumer.stop()
        except Exception as e:
            logger.error(f"Error stopping consumer: {e}")

        try:
            if messaging_service:
                messaging_service.disconnect()
                logger.info("Disconnected from Solace event broker")
        except Exception as e:
            logger.error(f"Error disconnecting: {e}")


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    main()
