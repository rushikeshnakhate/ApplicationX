#include "FixDictionaryGenerator.h"
#include <fstream>
#include <sstream>
#include <algorithm>

using namespace tinyxml2;

FixDictionaryGenerator::FixDictionaryGenerator() : document(new XMLDocument()) {}
FixDictionaryGenerator::~FixDictionaryGenerator() = default;

bool FixDictionaryGenerator::loadDictionary(const std::string& xmlFilePath) {
    if (document->LoadFile(xmlFilePath.c_str()) != XML_SUCCESS) {
        return false;
    }
    parseFields();
    parseMessages();
    parseComponents();
    return true;
}

void FixDictionaryGenerator::parseFields() {
    XMLElement* fieldsElem = document->FirstChildElement("fix")->FirstChildElement("fields");
    for (XMLElement* fieldElem = fieldsElem->FirstChildElement("field"); fieldElem; fieldElem = fieldElem->NextSiblingElement("field")) {
        FieldDefinition field;
        field.number = std::stoi(fieldElem->Attribute("number"));
        field.name = fieldElem->Attribute("name");
        field.type = fieldElem->Attribute("type");
        field.required = false;
        for (XMLElement* valueElem = fieldElem->FirstChildElement("value"); valueElem; valueElem = valueElem->NextSiblingElement("value")) {
            field.values[valueElem->Attribute("enum")] = valueElem->Attribute("description");
        }
        fields[field.name] = field;
    }
}

void FixDictionaryGenerator::parseMessages() {
    XMLElement* messagesElem = document->FirstChildElement("fix")->FirstChildElement("messages");
    for (XMLElement* msgElem = messagesElem->FirstChildElement("message"); msgElem; msgElem = msgElem->NextSiblingElement("message")) {
        MessageDefinition msg;
        msg.name = msgElem->Attribute("name");
        msg.msgType = msgElem->Attribute("msgtype");
        msg.msgCat = msgElem->Attribute("msgcat");
        for (XMLElement* fieldElem = msgElem->FirstChildElement("field"); fieldElem; fieldElem = fieldElem->NextSiblingElement("field")) {
            std::string fieldName = fieldElem->Attribute("name");
            bool required = std::string(fieldElem->Attribute("required")) == "Y";
            if (fields.count(fieldName)) {
                FieldDefinition field = fields[fieldName];
                field.required = required;
                msg.fields.push_back(field);
            }
        }
        messages[msg.msgType] = msg;
    }
}

void FixDictionaryGenerator::parseComponents() {
    XMLElement* compsElem = document->FirstChildElement("fix")->FirstChildElement("components");
    if (!compsElem) return;
    for (XMLElement* compElem = compsElem->FirstChildElement("component"); compElem; compElem = compElem->NextSiblingElement("component")) {
        ComponentDefinition comp;
        comp.name = compElem->Attribute("name");
        for (XMLElement* fieldElem = compElem->FirstChildElement("field"); fieldElem; fieldElem = fieldElem->NextSiblingElement("field")) {
            std::string fieldName = fieldElem->Attribute("name");
            bool required = std::string(fieldElem->Attribute("required")) == "Y";
            if (fields.count(fieldName)) {
                FieldDefinition field = fields[fieldName];
                field.required = required;
                comp.fields.push_back(field);
            }
        }
        components[comp.name] = comp;
    }
}

std::string FixDictionaryGenerator::toUpperCase(const std::string& str) {
    std::string result = str;
    std::transform(result.begin(), result.end(), result.begin(), ::toupper);
    return result;
}

std::string FixDictionaryGenerator::escapeString(const std::string& str) {
    std::string result = str;
    size_t pos = 0;
    while ((pos = result.find("\"", pos)) != std::string::npos) {
        result.replace(pos, 1, "\\\"");
        pos += 2;
    }
    return result;
}

std::string FixDictionaryGenerator::generateCppCode() {
    std::ostringstream code;
    code << "#pragma once\n#include <string>\n#include <map>\n#include <vector>\n\n";
    code << "class FixMessageCodec {\npublic:\n";
    code << "    // Field constants\n";
    for (const auto& [name, field] : fields) {
        code << "    static constexpr int " << toUpperCase(name) << " = " << field.number << ";\n";
    }
    code << "\n    // Message types\n";
    for (const auto& [msgType, msg] : messages) {
        code << "    static constexpr const char* " << toUpperCase(msg.name) << " = \"" << msgType << "\";\n";
    }
    code << "\n    static std::string encodeMessage(const std::string& msgType, const std::map<std::string, std::string>& fields);\n";
    code << "    static std::map<std::string, std::string> decodeMessage(const std::string& message);\n";
    code << "};\n";
    return code.str();
}

bool FixDictionaryGenerator::generateCodeToFile(const std::string& outputPath) {
    std::ofstream out(outputPath);
    if (!out) return false;
    out << generateCppCode();
    return true;
} 