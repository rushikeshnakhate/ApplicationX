#!/usr/bin/env python3

import json
import sys
from pathlib import Path

def parse_benchmark_results():
    results = {}
    benchmark_dir = Path(__file__).parent
    results_dir = benchmark_dir.parent / "benchmark_results"
    
    # Parse results for each language
    for lang in ["java", "cpp", "python"]:
        lang_results = {}
        lang_dir = results_dir / lang
        if lang_dir.exists():
            for result_file in lang_dir.glob("*.json"):
                with open(result_file) as f:
                    lang_results[result_file.stem] = json.load(f)
        results[lang] = lang_results
    
    # Save combined results
    output_file = results_dir / "combined_results.json"
    with open(output_file, "w") as f:
        json.dump(results, f, indent=2)
    
    print(f"Results saved to {output_file}")

if __name__ == "__main__":
    parse_benchmark_results()
