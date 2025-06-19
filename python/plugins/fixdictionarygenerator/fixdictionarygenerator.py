import xml.etree.ElementTree as ET
from typing import Dict, List

class FieldDefinition:
    def __init__(self, number, name, type_):
        self.number = int(number)
        self.name = name
        self.type = type_
        self.values = {}
        self.required = False

class MessageDefinition:
    def __init__(self, name, msgtype, msgcat):
        self.name = name
        self.msgtype = msgtype
        self.msgcat = msgcat
        self.fields: List[FieldDefinition] = []

class FixDictionaryGenerator:
    def __init__(self):
        self.fields: Dict[str, FieldDefinition] = {}
        self.messages: Dict[str, MessageDefinition] = {}

    def load_dictionary(self, xml_file_path):
        tree = ET.parse(xml_file_path)
        root = tree.getroot()
        self._parse_fields(root)
        self._parse_messages(root)

    def _parse_fields(self, root):
        for field_elem in root.find('fields').findall('field'):
            field = FieldDefinition(
                field_elem.attrib['number'],
                field_elem.attrib['name'],
                field_elem.attrib['type']
            )
            for value_elem in field_elem.findall('value'):
                field.values[value_elem.attrib['enum']] = value_elem.attrib['description']
            self.fields[field.name] = field

    def _parse_messages(self, root):
        for msg_elem in root.find('messages').findall('message'):
            msg = MessageDefinition(
                msg_elem.attrib['name'],
                msg_elem.attrib['msgtype'],
                msg_elem.attrib['msgcat']
            )
            for field_elem in msg_elem.findall('field'):
                field_name = field_elem.attrib['name']
                required = field_elem.attrib['required'] == 'Y'
                if field_name in self.fields:
                    field = self.fields[field_name]
                    field.required = required
                    msg.fields.append(field)
            self.messages[msg.msgtype] = msg

    def generate_python_code(self):
        code = ["class FixMessageCodec:"]
        code.append("    # Field constants")
        for field in self.fields.values():
            code.append(f"    {field.name.upper()} = {field.number}")
        code.append("")
        code.append("    # Message types")
        for msg in self.messages.values():
            code.append(f"    {msg.name.upper()} = '{msg.msgtype}'")
        code.append("")
        code.append("    @staticmethod")
        code.append("    def encode_message(msg_type, fields):")
        code.append("        parts = [f'8=FIX.4.4\\x01', f'35={msg_type}\\x01']")
        code.append("        for k, v in fields.items():")
        code.append("            if v:")
        code.append("                parts.append(f'{FixMessageCodec.get_field_number(k)}={v}\\x01')")
        code.append("        message = ''.join(parts)")
        code.append("        body_length = len(message)")
        code.append("        message = f'8=FIX.4.4\\x019={body_length:09d}\\x01' + message[8:]")
        code.append("        checksum = sum(ord(c) for c in message) % 256")
        code.append("        message += f'10={checksum:03d}\\x01'")
        code.append("        return message")
        code.append("")
        code.append("    @staticmethod")
        code.append("    def decode_message(message):")
        code.append("        result = {}")
        code.append("        for part in message.split('\\x01'):")
        code.append("            if '=' in part:")
        code.append("                field, value = part.split('=', 1)")
        code.append("                result[FixMessageCodec.get_field_name(field)] = value")
        code.append("        return result")
        code.append("")
        code.append("    @staticmethod")
        code.append("    def get_field_number(field_name):")
        code.append("        mapping = {")
        for field in self.fields.values():
            code.append(f"            '{field.name}': '{field.number}',")
        code.append("        }")
        code.append("        return mapping.get(field_name, '0')")
        code.append("")
        code.append("    @staticmethod")
        code.append("    def get_field_name(field_number):")
        code.append("        mapping = {")
        for field in self.fields.values():
            code.append(f"            '{field.number}': '{field.name}',")
        code.append("        }")
        code.append("        return mapping.get(field_number, 'Unknown')")
        return '\n'.join(code)

    def generate_code_to_file(self, output_path):
        code = self.generate_python_code()
        with open(output_path, 'w') as f:
            f.write(code) 