package com.applicationsx.trademessageprocessor;

import fixprocessor.TradeOuterClass;
import javax.jms.*;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

public class TradeProducer {
    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;

    public TradeProducer(String host, String vpn, String username, String password, String topicName) throws JMSException {
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setVPN(vpn);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        this.connection = connectionFactory.createConnection();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        this.producer = session.createProducer(topic);
        this.connection.start();
    }

    public void send(TradeOuterClass.Trade trade) throws JMSException {
        BytesMessage message = session.createBytesMessage();
        message.writeBytes(trade.toByteArray());
        producer.send(message);
    }

    public void close() throws JMSException {
        producer.close();
        session.close();
        connection.close();
    }
} 