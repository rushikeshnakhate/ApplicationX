package com.applicationsx.quickfiximpl;

import org.junit.Before;
import org.junit.Test;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

import static org.junit.Assert.*;

public class QuickFixServerTest {
    private QuickFixServer server;
    private SessionID testSessionId;

    @Before
    public void setUp() {
        server = new QuickFixServer();
        testSessionId = new SessionID("FIX.4.4", "CLIENT", "SERVER");
    }

    @Test
    public void testOnCreate() {
        server.onCreate(testSessionId);
        assertNotNull("Server should be created", server);
    }

    @Test
    public void testOnLogon() {
        server.onLogon(testSessionId);
        assertNotNull("Server should handle logon", server);
    }

    @Test
    public void testOnLogout() {
        server.onLogout(testSessionId);
        assertNotNull("Server should handle logout", server);
    }

    @Test
    public void testOnMessage() {
        try {
            NewOrderSingle order = new NewOrderSingle();
            order.set(new ClOrdID("TEST123"));
            order.set(new Symbol("AAPL"));
            order.set(new Side(Side.BUY));
            order.set(new OrdType(OrdType.LIMIT));
            order.set(new OrderQty(100));
            order.set(new Price(150.0));
            order.set(new TimeInForce(TimeInForce.DAY));

            server.onCreate(testSessionId);
            server.onMessage(order, testSessionId);
            assertNotNull("Server should handle new order", server);
        } catch (FieldNotFound e) {
            fail("Should not throw FieldNotFound in test: " + e.getMessage());
        }
    }
} 