package com.applicationsx.quickfiximpl;

import quickfix.*;

public class QuickFixDemo {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: QuickFixDemo [server|client]");
            System.exit(100);
        }

        String mode = args[0].toLowerCase();

        try {
            if ("server".equals(mode)) {
                runServer();
            } else if ("client".equals(mode)) {
                runClient();
            } else {
                System.out.println("Invalid mode. Use 'server' or 'client'");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runServer() throws Exception {
        System.out.println("Starting QuickFix Server...");

        // Load server configuration
        SessionSettings settings = new SessionSettings("src/main/resources/quickfix_server.cfg");

        // Create application
        QuickFixServer application = new QuickFixServer();

        // Create message store factory
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);

        // Create log factory
        LogFactory logFactory = new FileLogFactory(settings);

        // Create message factory
        MessageFactory messageFactory = new DefaultMessageFactory();

        // Create acceptor
        ThreadedSocketAcceptor acceptor = new ThreadedSocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);

        acceptor.start();
        System.out.println("Server started. Press Ctrl+C to stop.");

        // Keep server running
        Thread.currentThread().join();
    }

    private static void runClient() throws Exception {
        System.out.println("Starting QuickFix Client...");

        // Load client configuration
        SessionSettings settings = new SessionSettings("src/main/resources/quickfix_client.cfg");

        // Create application
        QuickFixClient application = new QuickFixClient();

        // Create message store factory
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);

        // Create log factory
        LogFactory logFactory = new FileLogFactory(settings);

        // Create message factory
        MessageFactory messageFactory = new DefaultMessageFactory();

        // Create initiator
        SocketInitiator initiator = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);

        initiator.start();
        System.out.println("Client started. Press Ctrl+C to stop.");

        // Keep client running
        Thread.currentThread().join();
    }
} 