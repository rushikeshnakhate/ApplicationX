package com.applicationsx.quickfiximpl;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;

import java.time.LocalDateTime;

public class QuickFixClient extends MessageCracker implements Application {
    private SessionID sessionId;

    @Override
    public void onCreate(SessionID sessionID) {
        this.sessionId = sessionID;
        System.out.println("Session created: " + sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        System.out.println("Logged on: " + sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        System.out.println("Logged out: " + sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        // Add admin messages if needed
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) {
        // Handle admin messages
    }

    @Override
    public void toApp(Message message, SessionID sessionID) {
        // Add application messages if needed
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) {
        try {
            crack(message, sessionID);
        } catch (UnsupportedMessageType | FieldNotFound | IncorrectTagValue e) {
            throw new RuntimeException(e);
        }
    }

    public void onMessage(ExecutionReport message, SessionID sessionID) {
        System.out.println("Received execution report: " + message);
    }

    public void sendNewOrder(String symbol, char side, double quantity, double price) throws SessionNotFound {
        NewOrderSingle order = new NewOrderSingle();
        order.set(new ClOrdID("ORDER-" + System.currentTimeMillis()));
        order.set(new Symbol(symbol));
        order.set(new Side(side));
        order.set(new OrdType(OrdType.LIMIT));
        order.set(new OrderQty(quantity));
        order.set(new Price(price));
        order.set(new TimeInForce(TimeInForce.DAY));
        order.set(new TransactTime(LocalDateTime.now()));

        Session.sendToTarget(order, sessionId);
    }
} 