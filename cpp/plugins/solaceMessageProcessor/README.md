# TradeMessageProcessor (C++)

This plugin provides a SOLID-compliant producer and consumer for Trade messages (NewOrder, Cancel, Amend) using Protobuf and Solace.

## Features
- Publishes and consumes Protobuf-encoded Trade messages over Solace
- Follows SOLID design principles
- Easily extensible for new message types

## Usage
- Build with CMake
- Configure Solace connection in `main.cpp`
- Run producer or consumer via `main.cpp`

## Protobuf Code Generation

To generate C++ classes from the Protobuf definition:

1. Ensure `fixProcessor/proto/Trade.proto` exists.
2. Run:

```sh
protoc -I../../fixProcessor/proto --cpp_out=. ../../fixProcessor/proto/Trade.proto
```

This will generate `Trade.pb.h` and `Trade.pb.cc` in the current directory. 