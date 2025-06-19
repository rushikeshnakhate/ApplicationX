import yaml
import sys
from typing import Any, Dict

def load_yaml(file_path: str) -> Dict[str, Any]:
    with open(file_path, 'r') as f:
        return yaml.safe_load(f)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python yaml_loader.py <file.yaml>")
        sys.exit(1)
    data = load_yaml(sys.argv[1])
    print(data) 