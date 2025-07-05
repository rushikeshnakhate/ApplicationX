import logging

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


class SolaceConfig:
    """Configuration class for Solace connection properties."""

    def __init__(self):
        self.broker_props = {
            "solace.messaging.transport.host": "tcp://localhost:55555",
            "solace.messaging.service.vpn-name": "default",
            "solace.messaging.authentication.scheme.basic.username": "default",
            "solace.messaging.authentication.scheme.basic.password": ""
        }
        self.topic_prefix = "trade"
        self.topics = {
            "new_order": f"{self.topic_prefix}/new_order",
            "cancel": f"{self.topic_prefix}/cancel",
            "amend": f"{self.topic_prefix}/amend"
        }
