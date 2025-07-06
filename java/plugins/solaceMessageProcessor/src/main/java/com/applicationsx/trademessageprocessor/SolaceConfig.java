package com.applicationsx.trademessageprocessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SolaceConfig {
    public static final String HOST = "tcp://localhost:55555";
    public static final String VPN = "default";
    public static final String USERNAME = "default";
    public static final String PASSWORD = "";

    public static final String TOPIC_PREFIX = "trade";
    public static final Map<String, String> TOPICS = new HashMap<>();

    public static final Properties brokerProps = new Properties();

    static {
        TOPICS.put("new_order", TOPIC_PREFIX + "/new_order");
        TOPICS.put("cancel", TOPIC_PREFIX + "/cancel");
        TOPICS.put("amend", TOPIC_PREFIX + "/amend");
        
        brokerProps.setProperty("solace.messaging.transport.host", HOST);
        brokerProps.setProperty("solace.messaging.service.vpn-name", VPN);
        brokerProps.setProperty("solace.messaging.authentication.scheme.basic.username", USERNAME);
        brokerProps.setProperty("solace.messaging.authentication.scheme.basic.password", PASSWORD);
    }
}
