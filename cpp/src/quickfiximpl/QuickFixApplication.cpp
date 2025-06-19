#include "QuickFixApplication.h"

QuickFixApplication::QuickFixApplication() : session(nullptr) {}

void QuickFixApplication::onCreate(const FIX::SessionID& sessionID) {
    session = FIX::Session::lookupSession(sessionID);
    std::cout << "Session created: " << sessionID << std::endl;
}

void QuickFixApplication::onLogon(const FIX::SessionID& sessionID) {
    std::cout << "Logged on: " << sessionID << std::endl;
}

void QuickFixApplication::onLogout(const FIX::SessionID& sessionID) {
    std::cout << "Logged out: " << sessionID << std::endl;
}

void QuickFixApplication::toAdmin(FIX::Message& message, const FIX::SessionID& sessionID) {
    // Add admin-level message handling if needed
}

void QuickFixApplication::fromAdmin(const FIX::Message& message, const FIX::SessionID& sessionID)
    throw(FIX::FieldNotFound, FIX::IncorrectDataFormat, FIX::IncorrectTagValue, FIX::RejectLogon) {
    // Handle admin-level messages
}

void QuickFixApplication::toApp(FIX::Message& message, const FIX::SessionID& sessionID)
    throw(FIX::DoNotSend) {
    // Add application-level message handling if needed
}

void QuickFixApplication::fromApp(const FIX::Message& message, const FIX::SessionID& sessionID)
    throw(FIX::FieldNotFound, FIX::IncorrectDataFormat, FIX::IncorrectTagValue, FIX::UnsupportedMessageType) {
    crack(message, sessionID);
}

void QuickFixApplication::onMessage(const FIX44::NewOrderSingle& message, const FIX::SessionID& sessionID)
    throw(FIX::FieldNotFound) {
    FIX::ClOrdID clOrdID;
    message.get(clOrdID);
    std::cout << "Received new order: " << clOrdID.getValue() << std::endl;
}

void QuickFixApplication::sendOrder(const FIX44::NewOrderSingle& order) {
    if (session) {
        FIX::Session::send(order);
    }
} 