#include "YamlLoader.h"
#include <iostream>

int main(int argc, char* argv[]) {
    if (argc < 2) {
        std::cout << "Usage: yaml_loader <file.yaml>" << std::endl;
        return 1;
    }
    auto node = yaml_loader::loadYaml(argv[1]);
    std::cout << node << std::endl;
    return 0;
} 