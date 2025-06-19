import os
from fixdictionarygenerator import FixDictionaryGenerator

def test_load_dictionary():
    gen = FixDictionaryGenerator()
    gen.load_dictionary('fix44_sample.xml')
    assert 'BeginString' in gen.fields
    assert 'D' in gen.messages

def test_generate_python_code():
    gen = FixDictionaryGenerator()
    gen.load_dictionary('fix44_sample.xml')
    code = gen.generate_python_code()
    assert 'FixMessageCodec' in code
    assert 'CLORDID' in code
    assert 'NewOrderSingle' in code or 'NEWORDERSINGLE' in code

def test_generate_code_to_file():
    gen = FixDictionaryGenerator()
    gen.load_dictionary('fix44_sample.xml')
    gen.generate_code_to_file('test_codec.py')
    assert os.path.exists('test_codec.py')
    with open('test_codec.py') as f:
        content = f.read()
        assert 'FixMessageCodec' in content
    os.remove('test_codec.py') 