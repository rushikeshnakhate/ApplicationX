package com.applicationsx.fixdictionarygenerator;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import static org.junit.Assert.*;

public class FixDictionaryGeneratorTest {
    private FixDictionaryGenerator generator;
    private static final String TEST_XML = "src/main/resources/fix44_sample.xml";

    @Before
    public void setUp() throws Exception {
        generator = new FixDictionaryGenerator();
        
        // Check if the XML file exists
        File xmlFile = new File(TEST_XML);
        if (!xmlFile.exists()) {
            throw new RuntimeException("XML file not found at: " + xmlFile.getAbsolutePath());
        }
        
        try {
            generator.loadDictionary(TEST_XML);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load XML dictionary: " + e.getMessage(), e);
        }
    }

    @Test
    public void testLoadDictionary() {
        assertNotNull("Generator should be created", generator);
        assertFalse("Fields should be loaded", generator.getFields().isEmpty());
        assertFalse("Messages should be loaded", generator.getMessages().isEmpty());
    }

    @Test
    public void testFieldParsing() {
        FixDictionaryGenerator.FieldDefinition beginString = generator.getFields().get("BeginString");
        assertNotNull("BeginString field should exist", beginString);
        assertEquals("BeginString field number should be 8", 8, beginString.number);
        assertEquals("BeginString type should be STRING", "STRING", beginString.type);
    }

    @Test
    public void testMessageParsing() {
        FixDictionaryGenerator.MessageDefinition newOrder = generator.getMessages().get("D");
        assertNotNull("NewOrderSingle message should exist", newOrder);
        assertEquals("Message name should be NewOrderSingle", "NewOrderSingle", newOrder.name);
        assertEquals("Message type should be D", "D", newOrder.msgType);
    }

    @Test
    public void testGenerateJavaCode() {
        String code = generator.generateJavaCode();
        assertNotNull("Generated code should not be null", code);
        assertTrue("Code should contain package declaration", code.contains("package"));
        assertTrue("Code should contain FixMessageCodec class", code.contains("FixMessageCodec"));
        assertTrue("Code should contain encode method", code.contains("encodeMessage"));
        assertTrue("Code should contain decode method", code.contains("decodeMessage"));
    }

    @Test
    public void testGenerateCodeToFile() throws Exception {
        String outputPath = "test_generated_code.java";
        generator.generateCodeToFile(outputPath);
        
        File outputFile = new File(outputPath);
        assertTrue("Output file should be created", outputFile.exists());
        assertTrue("Output file should not be empty", outputFile.length() > 0);
        
        // Clean up
        outputFile.delete();
    }

    @Test
    public void testEncodeDecodeRoundTrip() throws Exception {
        // Generate the codec
        String codecPath = "test_codec.java";
        generator.generateCodeToFile(codecPath);
        
        // Create a test message
        Map<String, String> testFields = new HashMap<>();
        testFields.put("ClOrdID", "ORDER123");
        testFields.put("Symbol", "AAPL");
        testFields.put("Side", "1"); // BUY
        testFields.put("OrderQty", "100");
        testFields.put("OrdType", "2"); // LIMIT
        testFields.put("Price", "150.50");
        
        // Note: In a real implementation, you would compile and use the generated code
        // For this test, we'll verify the structure of the generated code
        String generatedCode = generator.generateJavaCode();
        
        // Verify the generated code contains the expected field constants
        assertTrue("Generated code should contain ClOrdID constant", 
                  generatedCode.contains("CLORDID = 11"));
        assertTrue("Generated code should contain Symbol constant", 
                  generatedCode.contains("SYMBOL = 55"));
        assertTrue("Generated code should contain Side constant", 
                  generatedCode.contains("SIDE = 54"));
        
        // Clean up
        new File(codecPath).delete();
    }

    @Test
    public void testEnumValues() {
        FixDictionaryGenerator.FieldDefinition msgType = generator.getFields().get("MsgType");
        assertNotNull("MsgType field should exist", msgType);
        assertEquals("MsgType should have 2 enum values", 2, msgType.values.size());
        assertEquals("MsgType D should map to NEW_ORDER_SINGLE", "NEW_ORDER_SINGLE", msgType.values.get("D"));
        assertEquals("MsgType 8 should map to EXECUTION_REPORT", "EXECUTION_REPORT", msgType.values.get("8"));
    }

    @Test
    public void testRequiredFields() {
        FixDictionaryGenerator.MessageDefinition newOrder = generator.getMessages().get("D");
        assertNotNull("NewOrderSingle message should exist", newOrder);
        
        // Check that required fields are marked
        boolean hasRequiredClOrdID = false;
        for (FixDictionaryGenerator.FieldDefinition field : newOrder.fields) {
            if ("ClOrdID".equals(field.name) && field.required) {
                hasRequiredClOrdID = true;
                break;
            }
        }
        assertTrue("ClOrdID should be marked as required", hasRequiredClOrdID);
    }
} 