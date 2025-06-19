package com.applicationsx.ringbuffer;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class RingBufferApplication {
    public static class ValueEvent {
        private long value;
        private String data;
        private long timestamp;

        public long getValue() { return value; }
        public void setValue(long value) { this.value = value; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class ValueEventFactory implements com.lmax.disruptor.EventFactory<ValueEvent> {
        @Override
        public ValueEvent newInstance() {
            return new ValueEvent();
        }
    }

    public static class ValueEventHandler implements com.lmax.disruptor.EventHandler<ValueEvent> {
        @Override
        public void onEvent(ValueEvent event, long sequence, boolean endOfBatch) {
            System.out.println("Event: " + event.getValue() + " Data: " + event.getData());
        }
    }

    private Disruptor<ValueEvent> disruptor;
    private RingBuffer<ValueEvent> ringBuffer;

    public RingBufferApplication() {
        disruptor = new Disruptor<>(
            new ValueEventFactory(),
            1024,
            DaemonThreadFactory.INSTANCE
        );
        
        disruptor.handleEventsWith(new ValueEventHandler());
        ringBuffer = disruptor.start();
    }

    public void publishEvent(long value, String data) {
        long sequence = ringBuffer.next();
        try {
            ValueEvent event = ringBuffer.get(sequence);
            event.setValue(value);
            event.setData(data);
            event.setTimestamp(System.currentTimeMillis());
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public void shutdown() {
        disruptor.shutdown();
    }

    public static void main(String[] args) {
        RingBufferApplication app = new RingBufferApplication();
        
        for (int i = 0; i < 10; i++) {
            app.publishEvent(i, "Data-" + i);
        }
        
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        app.shutdown();
    }
} 