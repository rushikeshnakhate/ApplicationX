#include "FixDictionaryGenerator.h"
#include <iostream>

int main(int argc, char* argv[]) {
    if (argc < 3) {
        std::cout << "Usage: fixdictionarygenerator <input-xml> <output-h>" << std::endl;
        return 1;
    }
    FixDictionaryGenerator generator;
    if (!generator.loadDictionary(argv[1])) {
        std::cerr << "Failed to load dictionary: " << argv[1] << std::endl;
        return 1;
    }
    if (!generator.generateCodeToFile(argv[2])) {
        std::cerr << "Failed to write code to: " << argv[2] << std::endl;
        return 1;
    }
    std::cout << "Code generated successfully!" << std::endl;
    return 0;
} 