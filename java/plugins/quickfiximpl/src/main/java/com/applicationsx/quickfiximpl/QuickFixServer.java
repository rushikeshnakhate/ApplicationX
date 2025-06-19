package com.applicationsx.quickfiximpl;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;


public class QuickFixServer extends MessageCracker implements Application {
    private SessionID sessionId;

    @Override
    public void onCreate(SessionID sessionID) {
        this.sessionId = sessionID;
        System.out.println("Server session created: " + sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        System.out.println("Client logged on: " + sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        System.out.println("Client logged out: " + sessionID);
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

    public void onMessage(NewOrderSingle message, SessionID sessionID) throws FieldNotFound {
        System.out.println("Received new order: " + message);

        // Extract order details
        String clOrdID = message.get(new ClOrdID()).getValue();
        String symbol = message.get(new Symbol()).getValue();
        char side = message.get(new Side()).getValue();
        double quantity = message.get(new OrderQty()).getValue();
        double price = message.get(new Price()).getValue();

        System.out.println("Processing order: " + clOrdID + " for " + quantity + " " + symbol + " @ " + price);

        // Send execution report
        sendExecutionReport(clOrdID, symbol, side, quantity, price, OrdStatus.NEW);
    }

    private void sendExecutionReport(String clOrdID, String symbol, char side, double quantity, double price, char ordStatus) {
        try {
            ExecutionReport report = new ExecutionReport();
            report.set(new OrderID(clOrdID));
            report.set(new ExecID("EXEC-" + System.currentTimeMillis()));
            report.set(new ExecType(ExecType.NEW));
            report.set(new OrdStatus(ordStatus));
            report.set(new Symbol(symbol));
            report.set(new Side(side));
            report.set(new LeavesQty(quantity));
            report.set(new CumQty(0.0));
            report.set(new AvgPx(0.0));
            report.set(new ClOrdID(clOrdID));
            report.set(new TransactTime(java.time.LocalDateTime.now()));

            Session.sendToTarget(report, sessionId);
            System.out.println("Sent execution report for order: " + clOrdID);
        } catch (SessionNotFound e) {
            System.err.println("Session not found: " + e.getMessage());
        }
    }
} 