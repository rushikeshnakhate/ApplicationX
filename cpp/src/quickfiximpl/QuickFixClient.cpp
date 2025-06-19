#include "QuickFixClient.h"
#include <chrono>
#include <sstream>

QuickFixClient::QuickFixClient() : session(nullptr) {}

void QuickFixClient::onCreate(const FIX::SessionID& sessionID) {
    session = FIX::Session::lookupSession(sessionID);
    std::cout << "Client session created: " << sessionID << std::endl;
}

void QuickFixClient::onLogon(const FIX::SessionID& sessionID) {
    std::cout << "Client logged on: " << sessionID << std::endl;
}

void QuickFixClient::onLogout(const FIX::SessionID& sessionID) {
    std::cout << "Client logged out: " << sessionID << std::endl;
}

void QuickFixClient::toAdmin(FIX::Message& message, const FIX::SessionID& sessionID) {
    // Add admin-level message handling if needed
}

void QuickFixClient::fromAdmin(const FIX::Message& message, const FIX::SessionID& sessionID)
    throw(FIX::FieldNotFound, FIX::IncorrectDataFormat, FIX::IncorrectTagValue, FIX::RejectLogon) {
    // Handle admin-level messages
}

void QuickFixClient::toApp(FIX::Message& message, const FIX::SessionID& sessionID)
    throw(FIX::DoNotSend) {
    // Add application-level message handling if needed
}

void QuickFixClient::fromApp(const FIX::Message& message, const FIX::SessionID& sessionID)
    throw(FIX::FieldNotFound, FIX::IncorrectDataFormat, FIX::IncorrectTagValue, FIX::UnsupportedMessageType) {
    crack(message, sessionID);
}

void QuickFixClient::onMessage(const FIX44::ExecutionReport& message, const FIX::SessionID& sessionID)
    throw(FIX::FieldNotFound) {
    FIX::ClOrdID clOrdID;
    message.get(clOrdID);
    std::cout << "Received execution report: " << clOrdID.getValue() << std::endl;
}

void QuickFixClient::sendNewOrder(const std::string& symbol, char side, double quantity, double price) {
    FIX44::NewOrderSingle order;
    
    // Generate unique order ID
    auto now = std::chrono::system_clock::now();
    auto now_ms = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch());
    std::stringstream ss;
    ss << "ORDER" << now_ms.count();
    
    order.set(FIX::ClOrdID(ss.str()));
    order.set(FIX::HandlInst('1'));
    order.set(FIX::Symbol(symbol));
    order.set(FIX::Side(side));
    order.set(FIX::TransactTime());
    order.set(FIX::OrdType(FIX::OrdType_LIMIT));
    order.set(FIX::TimeInForce(FIX::TimeInForce_DAY));
    order.set(FIX::OrderQty(quantity));
    order.set(FIX::Price(price));

    if (session) {
        FIX::Session::send(order);
    }
} 