package com.applicationsx.fixdictionarygenerator;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

/**
 * FIX Dictionary Generator - Parses FIX XML dictionaries and generates Java code
 * for encoding/decoding FIX messages.
 * 
 * <p>This utility reads FIX protocol XML dictionary files and generates Java code
 * that can be used to encode and decode FIX messages according to the specification.</p>
 * 
 * @author ApplicationX
 * @version 1.0
 */
public class FixDictionaryGenerator {
    private Document document;
    private final Map<String, FieldDefinition> fields = new HashMap<>();
    private final Map<String, MessageDefinition> messages = new HashMap<>();
    private final Map<String, ComponentDefinition> components = new HashMap<>();

    /**
     * Represents a FIX field definition from the XML dictionary.
     * Contains field number, name, type, and possible enum values.
     */
    public static class FieldDefinition {
        /** The FIX field number */
        public int number;
        /** The field name */
        public String name;
        /** The field data type */
        public String type;
        /** Map of enum values to descriptions */
        public Map<String, String> values = new HashMap<>();
        /** Whether this field is required in messages */
        public boolean required;
    }

    /**
     * Represents a FIX message definition from the XML dictionary.
     * Contains message type, category, and associated fields.
     */
    public static class MessageDefinition {
        /** The message name */
        public String name;
        /** The message type identifier */
        public String msgType;
        /** The message category */
        public String msgCat;
        /** List of fields in this message */
        public List<FieldDefinition> fields = new ArrayList<>();
    }

    /**
     * Represents a FIX component definition from the XML dictionary.
     * Components are reusable field groups that can be included in multiple messages.
     */
    public static class ComponentDefinition {
        /** The component name */
        public String name;
        /** List of fields in this component */
        public List<FieldDefinition> fields = new ArrayList<>();
    }

    /**
     * Gets a copy of all field definitions.
     * 
     * @return Map of field names to field definitions
     */
    public Map<String, FieldDefinition> getFields() {
        return new HashMap<>(fields);
    }

    /**
     * Gets a copy of all message definitions.
     * 
     * @return Map of message types to message definitions
     */
    public Map<String, MessageDefinition> getMessages() {
        return new HashMap<>(messages);
    }

    /**
     * Gets a copy of all component definitions.
     * 
     * @return Map of component names to component definitions
     */
    public Map<String, ComponentDefinition> getComponents() {
        return new HashMap<>(components);
    }

    /**
     * Loads and parses a FIX XML dictionary file.
     * 
     * @param xmlFilePath Path to the XML dictionary file
     * @throws Exception if the file cannot be parsed or is invalid
     */
    public void loadDictionary(String xmlFilePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new File(xmlFilePath));
        document.getDocumentElement().normalize();

        parseFields();
        parseMessages();
        parseComponents();
    }

    private void parseFields() {
        Element fieldsElement = (Element) document.getDocumentElement().getElementsByTagName("fields").item(0);
        if (fieldsElement == null) {
            throw new RuntimeException("No <fields> section found in XML");
        }

        NodeList fieldNodes = fieldsElement.getChildNodes();
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Node node = fieldNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element fieldElement = (Element) node;
            if (!"field".equals(fieldElement.getTagName())) continue;
            FieldDefinition field = new FieldDefinition();

            try {
                String numberStr = fieldElement.getAttribute("number");
                if (numberStr == null || numberStr.trim().isEmpty()) {
                    System.err.println("Warning: Field at index " + i + " has no number attribute");
                    continue;
                }
                field.number = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing field number for field at index " + i + ": " + e.getMessage());
                throw new RuntimeException("Failed to parse field number: " + fieldElement.getAttribute("number"), e);
            }

            field.name = fieldElement.getAttribute("name");
            field.type = fieldElement.getAttribute("type");

            // Parse enum values
            NodeList valueNodes = fieldElement.getElementsByTagName("value");
            for (int j = 0; j < valueNodes.getLength(); j++) {
                Element valueElement = (Element) valueNodes.item(j);
                String enumValue = valueElement.getAttribute("enum");
                String description = valueElement.getAttribute("description");
                field.values.put(enumValue, description);
            }

            fields.put(field.name, field);
        }
    }

    private void parseMessages() {
        NodeList messageNodes = document.getElementsByTagName("message");
        for (int i = 0; i < messageNodes.getLength(); i++) {
            Element messageElement = (Element) messageNodes.item(i);
            MessageDefinition message = new MessageDefinition();

            message.name = messageElement.getAttribute("name");
            message.msgType = messageElement.getAttribute("msgtype");
            message.msgCat = messageElement.getAttribute("msgcat");

            // Parse message fields
            NodeList fieldNodes = messageElement.getElementsByTagName("field");
            for (int j = 0; j < fieldNodes.getLength(); j++) {
                Element fieldElement = (Element) fieldNodes.item(j);
                String fieldName = fieldElement.getAttribute("name");
                boolean required = "Y".equals(fieldElement.getAttribute("required"));

                FieldDefinition field = fields.get(fieldName);
                if (field != null) {
                    field.required = required;
                    message.fields.add(field);
                }
            }

            messages.put(message.msgType, message);
        }
    }

    private void parseComponents() {
        NodeList componentNodes = document.getElementsByTagName("component");
        for (int i = 0; i < componentNodes.getLength(); i++) {
            Element componentElement = (Element) componentNodes.item(i);
            ComponentDefinition component = new ComponentDefinition();

            component.name = componentElement.getAttribute("name");

            // Parse component fields
            NodeList fieldNodes = componentElement.getElementsByTagName("field");
            for (int j = 0; j < fieldNodes.getLength(); j++) {
                Element fieldElement = (Element) fieldNodes.item(j);
                String fieldName = fieldElement.getAttribute("name");
                boolean required = "Y".equals(fieldElement.getAttribute("required"));

                FieldDefinition field = fields.get(fieldName);
                if (field != null) {
                    field.required = required;
                    component.fields.add(field);
                }
            }

            components.put(component.name, component);
        }
    }

    /**
     * Generates Java code for FIX message encoding/decoding based on the loaded dictionary.
     * 
     * @return Generated Java source code as a string
     */
    public String generateJavaCode() {
        StringBuilder code = new StringBuilder();
        code.append("package com.applicationsx.fixdictionarygenerator.generated;\n\n");
        code.append("import java.util.*;\n");
        code.append("import java.time.*;\n\n");

        // Generate field constants
        code.append("public class FixMessageCodec {\n\n");
        code.append("    // Field constants\n");
        for (FieldDefinition field : fields.values()) {
            code.append("    public static final int ").append(field.name.toUpperCase()).append(" = ").append(field.number).append(";\n");
        }
        code.append("\n");

        // Generate message types
        code.append("    // Message types\n");
        for (MessageDefinition message : messages.values()) {
            code.append("    public static final String ").append(message.name.toUpperCase()).append(" = \"").append(message.msgType).append("\";\n");
        }
        code.append("\n");

        // Generate encode method
        code.append("    public static String encodeMessage(String msgType, Map<String, String> fields) {\n");
        code.append("        StringBuilder sb = new StringBuilder();\n");
        code.append("        sb.append(\"8=FIX.4.4\\u0001\");\n");
        code.append("        sb.append(\"35=\").append(msgType).append(\"\\u0001\");\n");
        code.append("        \n");
        code.append("        for (Map.Entry<String, String> entry : fields.entrySet()) {\n");
        code.append("            String fieldName = entry.getKey();\n");
        code.append("            String value = entry.getValue();\n");
        code.append("            if (value != null && !value.isEmpty()) {\n");
        code.append("                sb.append(getFieldNumber(fieldName)).append(\"=\").append(value).append(\"\\u0001\");\n");
        code.append("            }\n");
        code.append("        }\n");
        code.append("        \n");
        code.append("        String message = sb.toString();\n");
        code.append("        int bodyLength = message.length();\n");
        code.append("        String bodyLengthStr = String.format(\"%09d\", bodyLength);\n");
        code.append("        \n");
        code.append("        message = \"8=FIX.4.4\\u00019=\" + bodyLengthStr + \"\\u0001\" + message.substring(8);\n");
        code.append("        \n");
        code.append("        int checksum = calculateChecksum(message);\n");
        code.append("        message += \"10=\" + String.format(\"%03d\", checksum) + \"\\u0001\";\n");
        code.append("        \n");
        code.append("        return message;\n");
        code.append("    }\n\n");

        // Generate decode method
        code.append("    public static Map<String, String> decodeMessage(String message) {\n");
        code.append("        Map<String, String> result = new HashMap<>();\n");
        code.append("        String[] parts = message.split(\"\\u0001\");\n");
        code.append("        \n");
        code.append("        for (String part : parts) {\n");
        code.append("            if (part.contains(\"=\")) {\n");
        code.append("                String[] field = part.split(\"=\", 2);\n");
        code.append("                if (field.length == 2) {\n");
        code.append("                    String fieldNumber = field[0];\n");
        code.append("                    String value = field[1];\n");
        code.append("                    result.put(getFieldName(fieldNumber), value);\n");
        code.append("                }\n");
        code.append("            }\n");
        code.append("        }\n");
        code.append("        \n");
        code.append("        return result;\n");
        code.append("    }\n\n");

        // Generate helper methods
        code.append("    private static String getFieldNumber(String fieldName) {\n");
        code.append("        switch (fieldName) {\n");
        for (FieldDefinition field : fields.values()) {
            code.append("            case \"").append(field.name).append("\": return \"").append(field.number).append("\";\n");
        }
        code.append("            default: return \"0\";\n");
        code.append("        }\n");
        code.append("    }\n\n");

        code.append("    private static String getFieldName(String fieldNumber) {\n");
        code.append("        switch (fieldNumber) {\n");
        for (FieldDefinition field : fields.values()) {
            code.append("            case \"").append(field.number).append("\": return \"").append(field.name).append("\";\n");
        }
        code.append("            default: return \"Unknown\";\n");
        code.append("        }\n");
        code.append("    }\n\n");

        code.append("    private static int calculateChecksum(String message) {\n");
        code.append("        int sum = 0;\n");
        code.append("        for (char c : message.toCharArray()) {\n");
        code.append("            sum += c;\n");
        code.append("        }\n");
        code.append("        return sum % 256;\n");
        code.append("    }\n");
        code.append("}\n");

        return code.toString();
    }

    /**
     * Generates Java code and writes it to a file.
     * 
     * @param outputPath Path where the generated Java file should be written
     * @throws IOException if the file cannot be written
     */
    public void generateCodeToFile(String outputPath) throws IOException {
        String code = generateJavaCode();
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(code);
        }
    }

    /**
     * Main method for command-line usage.
     * 
     * <p>Usage: FixDictionaryGenerator &lt;input-xml&gt; &lt;output-java&gt;</p>
     * 
     * @param args Command line arguments: input XML file and output Java file
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: FixDictionaryGenerator <input-xml> <output-java>");
            System.exit(1);
        }

        try {
            FixDictionaryGenerator generator = new FixDictionaryGenerator();
            generator.loadDictionary(args[0]);
            generator.generateCodeToFile(args[1]);
            System.out.println("Code generated successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 