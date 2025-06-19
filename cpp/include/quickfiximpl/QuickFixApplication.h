#pragma once
#include <quickfix/Application.h>
#include <quickfix/MessageCracker.h>
#include <quickfix/Session.h>
#include <quickfix/fix44/NewOrderSingle.h>
#include <iostream>

class QuickFixApplication : public FIX::Application, public FIX::MessageCracker {
public:
    QuickFixApplication();
    virtual ~QuickFixApplication() = default;

    void onCreate(const FIX::SessionID& sessionID) override;
    void onLogon(const FIX::SessionID& sessionID) override;
    void onLogout(const FIX::SessionID& sessionID) override;
    void toAdmin(FIX::Message& message, const FIX::SessionID& sessionID) override;
    void fromAdmin(const FIX::Message& message, const FIX::SessionID& sessionID) override;
    void toApp(FIX::Message& message, const FIX::SessionID& sessionID) override;
    void fromApp(const FIX::Message& message, const FIX::SessionID& sessionID) override;

    void onMessage(const FIX44::NewOrderSingle& message, const FIX::SessionID& sessionID);
    void sendOrder(const FIX44::NewOrderSingle& order);

private:
    FIX::Session* session;
}; 