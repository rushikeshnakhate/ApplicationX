#pragma once
#include <quickfix/Application.h>
#include <quickfix/MessageCracker.h>
#include <quickfix/Session.h>
#include <quickfix/fix44/NewOrderSingle.h>
#include <quickfix/fix44/ExecutionReport.h>
#include <iostream>
#include <string>

class QuickFixClient : public FIX::Application, public FIX::MessageCracker {
public:
    QuickFixClient();
    virtual ~QuickFixClient() = default;

    void onCreate(const FIX::SessionID& sessionID) override;
    void onLogon(const FIX::SessionID& sessionID) override;
    void onLogout(const FIX::SessionID& sessionID) override;
    void toAdmin(FIX::Message& message, const FIX::SessionID& sessionID) override;
    void fromAdmin(const FIX::Message& message, const FIX::SessionID& sessionID) override;
    void toApp(FIX::Message& message, const FIX::SessionID& sessionID) override;
    void fromApp(const FIX::Message& message, const FIX::SessionID& sessionID) override;

    void onMessage(const FIX44::ExecutionReport& message, const FIX::SessionID& sessionID);
    void sendNewOrder(const std::string& symbol, char side, double quantity, double price);

private:
    FIX::Session* session;
}; 