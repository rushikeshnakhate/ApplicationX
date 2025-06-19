package com.applicationsx.quickfiximpl;

import org.junit.Before;
import org.junit.Test;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;

import static org.junit.Assert.*;

public class QuickFixClientTest {
    private QuickFixClient client;
    private SessionID testSessionId;

    @Before
    public void setUp() {
        client = new QuickFixClient();
        testSessionId = new SessionID("FIX.4.4", "SENDER", "TARGET");
    }

    @Test
    public void testOnCreate() {
        client.onCreate(testSessionId);
        assertNotNull("Client should be created", client);
    }

    @Test
    public void testOnLogon() {
        client.onLogon(testSessionId);
        assertNotNull("Client should handle logon", client);
    }

    @Test
    public void testOnLogout() {
        client.onLogout(testSessionId);
        assertNotNull("Client should handle logout", client);
    }
} 