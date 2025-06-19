# FixDictionaryGenerator (Python)

This plugin parses a FIX XML dictionary and generates Python code for encoding/decoding FIX messages.

## Usage

```python
from fixdictionarygenerator import FixDictionaryGenerator

gen = FixDictionaryGenerator()
gen.load_dictionary('fix44_sample.xml')
code = gen.generate_python_code()
with open('FixMessageCodec.py', 'w') as f:
    f.write(code)
```

## Testing

```sh
pytest test_fixdictionarygenerator.py
``` 