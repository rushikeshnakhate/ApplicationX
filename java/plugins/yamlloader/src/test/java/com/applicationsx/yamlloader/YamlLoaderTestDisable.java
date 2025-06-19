package com.applicationsx.yamlloader;

import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class YamlLoaderTestDisable {
    private YamlLoader loader;

    @Before
    public void setUp() {
        loader = new YamlLoader();
    }

    @Test
    public void testLoadFromString() {
        String yamlContent = "name: Test\nversion: 1.0";
        Map<String, Object> result = loader.loadFromString(yamlContent);
        
        assertNotNull("Result should not be null", result);
        assertEquals("name should be Test", "Test", result.get("name"));
        assertEquals("version should be 1.0", "1.0", String.valueOf(result.get("version")));
    }

    @Test
    public void testDumpToYaml() {
        Map<String, Object> data = Map.of("name", "Test", "version", "1.0");
        String yaml = loader.dumpToYaml(data);
        
        assertNotNull("YAML should not be null", yaml);
        assertTrue("YAML should contain name", yaml.contains("name"));
        assertTrue("YAML should contain version", yaml.contains("version"));
    }
} 