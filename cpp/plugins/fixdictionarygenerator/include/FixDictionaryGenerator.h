#pragma once

#include <string>
#include <vector>
#include <map>
#include <memory>
#include <tinyxml2.h>

class FixDictionaryGenerator {
public:
    struct FieldDefinition {
        int number;
        std::string name;
        std::string type;
        std::map<std::string, std::string> values;
        bool required;
    };

    struct MessageDefinition {
        std::string name;
        std::string msgType;
        std::string msgCat;
        std::vector<FieldDefinition> fields;
    };

    struct ComponentDefinition {
        std::string name;
        std::vector<FieldDefinition> fields;
    };

    FixDictionaryGenerator();
    ~FixDictionaryGenerator();

    bool loadDictionary(const std::string& xmlFilePath);
    std::string generateCppCode();
    bool generateCodeToFile(const std::string& outputPath);

private:
    std::unique_ptr<tinyxml2::XMLDocument> document;
    std::map<std::string, FieldDefinition> fields;
    std::map<std::string, MessageDefinition> messages;
    std::map<std::string, ComponentDefinition> components;

    void parseFields();
    void parseMessages();
    void parseComponents();
    std::string toUpperCase(const std::string& str);
    std::string escapeString(const std::string& str);
}; 