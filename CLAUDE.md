# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
mvn compile          # Compile the project
mvn test             # Run tests (none exist yet — src/test is absent)
mvn package          # Package into target/van-bridge-1.0-SNAPSHOT.jar
```

Java 8 source/target. No build plugins configured beyond the default Maven JAR plugin.

## Architecture

This is a **lightweight library for calling third-party REST APIs** over OkHttp. The base package is `io.github.vanplanifolia.vanbridge`.

### Core pattern: Template Method / Callback

The framework uses a callback-based abstraction. Consumers implement `ApiCallback<R>` (typically as an anonymous class) and pass it to `ApiTemplate.execute(callback)`, which handles error normalization and returns a standardized `ApiResponse<R>`.

```
ApiCallback<R>.call()          →  raw HTTP (implementor's job)
ApiTemplate.execute(callback)  →  error handling + response wrapping → ApiResponse<R>
HttpSender.doOkHttp*(...)      →  static OkHttp helpers (POST/GET, JSON/form/query params)
```

### Key classes

| Class | Role |
|---|---|
| `ApiTemplate` | Top-level entry point. Wraps a callback execution with try/catch and returns `ApiResponse`. |
| `ApiCallback<R>` | Interface. Implement `call()` (make the HTTP request), `getAbility()` (API identifier string), and `getTypeReference()` (fastjson2 `TypeReference<R>` for generic-aware deserialization). Default `getData()` methods handle JSON parsing. |
| `HttpSender` | Static OkHttp utilities — `doOkHttpPost` / `doOkHttpGet` with various overloads. 10s connect timeout, 30s read timeout. |
| `ApiResponse<R>` | Standardized response wrapper: `code`, `success`, `result`, `message`. Static factories `success(R)` and `fail(String)`. |
| `ThirdAbility` | Interface for capability enums — `getName()` and `getUri()`. Consumers define an enum listing their third-party's endpoints. |
| `ApiRequestModel` | Interface for request DTOs with default `toRequestJSON()` / `toRequestMap()` serialization methods. |
| `ApiResultModel` | Marker interface for response DTOs (no methods). Used as the generic bound for `R` throughout. |
| `ApiException` | Unchecked exception (`RuntimeException`) for API call failures. |
| `ApiServiceTemplate` | Interface contract for `doRequest(ThirdAbility, ApiRequestModel)`. Intended for consumer implementation. |

### Dependencies

- **OkHttp 4.9.3** — HTTP client
- **fastjson2 2.0.57** — JSON serialization/deserialization, `TypeReference` for generic type capture
- **Lombok 1.18.38** (provided scope) — `@Data`, `@Slf4j`, `@Accessors`

### TypeReference pattern

`ApiCallback.getTypeReference()` returns a fastjson2 `TypeReference<R>`, which captures generic type info at runtime. This allows `getData()` to deserialize JSON into the correct concrete type without the caller passing a `Class<R>`. The canonical usage is an anonymous `ApiCallback` subclass:

```java
new ApiCallback<SomeResultModel>() {
    public TypeReference<SomeResultModel> getTypeReference() {
        return new TypeReference<SomeResultModel>() {};
    }
}
```

## Known Issues

- **Dead code**: `ThirdResultModelConverter.resultConverter()` is never called within this codebase.
- **No tests** — `src/test` does not exist yet.

## Skills

Integration guide and localized documentation live in [`skills/`](skills/):
- [`CLAUDE.md`](skills/CLAUDE.md) — English version of this file
- [`CLAUDE_zh.md`](skills/CLAUDE_zh.md) — Chinese version
- [`VAN_BRIDGE_INTEGRATION_GUIDE.md`](skills/VAN_BRIDGE_INTEGRATION_GUIDE.md) — Step-by-step quick-start guide with code examples