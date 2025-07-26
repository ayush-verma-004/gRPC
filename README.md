# 📚 gRPC Notes (For Revision)

A quick and concise reference for understanding and revising gRPC concepts, especially useful for Java/Spring Boot developers.

---

## 🔹 What is gRPC?

- **gRPC (Google Remote Procedure Call)** is an open-source, high-performance remote procedure call (RPC) framework.
- It enables client-server communication using **Protocol Buffers (protobuf)**.
- Designed to be **fast**, **efficient**, and **language-agnostic**.
- Uses **HTTP/2** for transport, enabling multiplexing and streaming.

---

## 🔸 Key Components

| Component     | Description                                                                 |
|--------------|-----------------------------------------------------------------------------|
| `.proto` file | Contract that defines service, RPC methods, and message schemas            |
| Stub          | Auto-generated client and server code for method calling                   |
| Channel       | Represents a connection to a gRPC server                                   |
| Service       | Implementation of methods declared in proto                                |
| Interceptor   | Similar to Spring filters; used for logging, auth, etc.                    |

---

## 🔹 Types of RPCs

1. **Unary RPC**
   - One request → One response.
   - Most common type.

2. **Server Streaming**
   - One request → Stream of responses.
   - Server sends multiple responses.

3. **Client Streaming**
   - Stream of requests → One response.
   - Client sends multiple messages.

4. **Bidirectional Streaming**
   - Stream of requests ↔ Stream of responses.
   - Client and server send/receive in parallel.

---

## 🔸 gRPC vs REST

| Feature        | REST             | gRPC                     |
|----------------|------------------|--------------------------|
| Protocol       | HTTP/1.1         | HTTP/2                   |
| Payload Format | JSON             | Protocol Buffers (binary)|
| Performance    | Slower           | Faster                   |
| Contract       | OpenAPI/Swagger  | .proto files             |
| Streaming      | Limited          | Native support (HTTP/2)  |
| Browser Support| Native           | Needs proxy              |

---

## 🔹 .proto Syntax Example

```proto
syntax = "proto3";

package com.example;

service Greeter {
  rpc SayHello (HelloRequest) returns (HelloReply);
}

message HelloRequest {
  string name = 1;
}

message HelloReply {
  string message = 1;
}

