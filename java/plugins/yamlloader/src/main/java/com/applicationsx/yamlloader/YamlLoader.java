package com.applicationsx.yamlloader;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class YamlLoader {
    private final Yaml yaml;

    public YamlLoader() {
        this.yaml = new Yaml();
    }

    public Map<String, Object> loadFromFile(String filePath) throws FileNotFoundException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading YAML file: " + filePath, e);
        }
    }

    public Map<String, Object> loadFromString(String yamlContent) {
        return yaml.load(yamlContent);
    }

    public String dumpToYaml(Object object) {
        return yaml.dump(object);
    }

    public static void main(String[] args) {
        YamlLoader loader = new YamlLoader();
        
        // Example YAML content
        String yamlContent = """
            name: Test Application
            version: 1.0.0
            config:
              database:
                host: localhost
                port: 5432
              features:
                - feature1
                - feature2
            """;
        
        try {
            Map<String, Object> data = loader.loadFromString(yamlContent);
            System.out.println("Loaded YAML data: " + data);
            
            String dumped = loader.dumpToYaml(data);
            System.out.println("Dumped YAML:\n" + dumped);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
} 