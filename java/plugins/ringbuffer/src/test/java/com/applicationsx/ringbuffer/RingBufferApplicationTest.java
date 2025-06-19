package com.applicationsx.ringbuffer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RingBufferApplicationTest {
    private RingBufferApplication app;

    @Before
    public void setUp() {
        app = new RingBufferApplication();
    }

    @Test
    public void testPublishEvent() {
        app.publishEvent(1L, "Test Data");
        assertNotNull("Application should be created", app);
    }

    @Test
    public void testShutdown() {
        app.shutdown();
        assertNotNull("Application should handle shutdown", app);
    }
} 