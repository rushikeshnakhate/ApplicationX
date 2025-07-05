import logging

from Trade_pb2 import Trade
from solace.messaging.receiver.inbound_message import InboundMessage
from solace.messaging.receiver.message_receiver import MessageHandler as SolaceMessageHandler

logger = logging.getLogger(__name__)


class MessageHandler(SolaceMessageHandler):
    def on_message(self, message: InboundMessage):
        try:
            topic = message.get_destination_name()
            payload = message.get_payload_as_bytes()
            trade = Trade()
            trade.ParseFromString(payload)

            logger.info(f"on_message,topic={topic},payload={payload}")

            if trade.HasField("new_order"):
                order = trade.new_order
                logger.info(
                    f"Received NewOrder on topic {topic}: "
                    f"order_id={order.order_id}, symbol={order.symbol}, "
                    f"quantity={order.quantity}, price={order.price}"
                )
            elif trade.HasField("cancel"):
                cancel = trade.cancel
                logger.info(
                    f"Received Cancel on topic {topic}: "
                    f"order_id={cancel.order_id}"
                )
            elif trade.HasField("amend"):
                amend = trade.amend
                logger.info(
                    f"Received Amend on topic {topic}: "
                    f"order_id={amend.order_id}, "
                    f"new_quantity={amend.new_quantity}, "
                    f"new_price={amend.new_price}"
                )
            else:
                logger.warning(f"Received message on unexpected topic {topic}")

        except Exception as e:
            topic = message.get_destination_name() if message else "unknown"
            logger.error(
                f"Error processing message on topic {topic}: {e}",
                exc_info=True
            )
