#include "YamlLoader.h"

namespace yaml_loader {
    YAML::Node loadYaml(const std::string& filePath) {
        return YAML::LoadFile(filePath);
    }
} 