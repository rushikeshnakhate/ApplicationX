#include "TradeMessageBuilder.cpp"
#include "Producer.cpp"
#include "Consumer.cpp"
#include <iostream>

int main(int argc, char* argv[]) {
    if (argc < 2) {
        std::cout << "Usage: main produce|consume" << std::endl;
        return 1;
    }
    std::string host = "tcp://localhost:55555";
    std::string vpn = "default";
    std::string username = "admin";
    std::string password = "admin";
    std::string topic = "trades";
    if (std::string(argv[1]) == "produce") {
        TradeProducer producer(host, vpn, username, password, topic);
        auto trade = TradeMessageBuilder::NewOrder("ORD123", "AAPL", 100, 150.5);
        producer.send(trade);
        std::cout << "Produced Trade: " << trade.DebugString() << std::endl;
    } else if (std::string(argv[1]) == "consume") {
        TradeConsumer consumer(host, vpn, username, password, topic);
        std::cout << "Listening for trades. Press Ctrl+C to exit." << std::endl;
        consumer.start();
    } else {
        std::cout << "Unknown mode: " << argv[1] << std::endl;
    }
    return 0;
} 