package com.applicationsx.trademessageprocessor;

import fixprocessor.TradeOuterClass;
import javax.jms.*;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

public class TradeConsumer implements MessageListener {
    private final Connection connection;
    private final Session session;
    private final MessageConsumer consumer;

    public TradeConsumer(String host, String vpn, String username, String password, String topicName) throws JMSException {
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setVPN(vpn);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        this.connection = connectionFactory.createConnection();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        this.consumer = session.createConsumer(topic);
        this.consumer.setMessageListener(this);
        this.connection.start();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof BytesMessage) {
            try {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] data = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(data);
                TradeOuterClass.Trade trade = TradeOuterClass.Trade.parseFrom(data);
                System.out.println("Received Trade: " + trade);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws JMSException {
        consumer.close();
        session.close();
        connection.close();
    }
} 