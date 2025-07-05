# TradeMessageProcessor (Java)

This plugin provides a SOLID-compliant producer and consumer for Trade messages (NewOrder, Cancel, Amend) using Protobuf and Solace.

## Features
- Publishes and consumes Protobuf-encoded Trade messages over Solace
- Follows SOLID design principles
- Easily extensible for new message types

## Usage
- Build with Gradle
- Configure Solace connection in `Main.java`
- Run producer or consumer via `Main.java`

## Protobuf Code Generation

To generate Java classes from the Protobuf definition:

1. Ensure `fixProcessor/proto/Trade.proto` exists.
2. In `build.gradle`, the protobuf plugin is already configured.
3. Run:

```sh
./gradlew generateProto
```

This will generate Java classes in `src/generated/main/java/fixprocessor/`. 