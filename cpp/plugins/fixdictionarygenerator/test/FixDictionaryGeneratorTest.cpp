#include "FixDictionaryGenerator.h"
#include <cassert>
#include <iostream>

int main() {
    FixDictionaryGenerator generator;
    bool loaded = generator.loadDictionary("../resources/fix44_sample.xml");
    assert(loaded && "Dictionary should load successfully");

    std::string code = generator.generateCppCode();
    assert(!code.empty() && "Generated code should not be empty");
    assert(code.find("FixMessageCodec") != std::string::npos && "Code should contain FixMessageCodec");
    assert(code.find("CLORDID") != std::string::npos && "Code should contain CLORDID constant");

    bool written = generator.generateCodeToFile("test_codec.h");
    assert(written && "Code should be written to file");

    std::cout << "All FixDictionaryGenerator C++ tests passed!" << std::endl;
    return 0;
} 