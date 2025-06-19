#pragma once
#include <yaml-cpp/yaml.h>
#include <string>

namespace yaml_loader {
    YAML::Node loadYaml(const std::string& filePath);
} 