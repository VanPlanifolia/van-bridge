# CLAUDE.md (中文版)

本文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指导。

## 构建命令

```bash
mvn compile          # 编译项目
mvn test             # 运行测试（暂无测试 — src/test 目录不存在）
mvn package          # 打包至 target/van-bridge-1.0-SNAPSHOT.jar
```

Java 8 源码/目标版本。除默认 Maven JAR 插件外，未配置其他构建插件。

## 架构

这是一个基于 OkHttp 的**轻量级第三方 REST API 调用库**。基础包名为 `io.github.vanplanifolia.vanbridge`。

### 核心模式：模板方法 / 回调

框架采用基于回调的抽象。使用者实现 `ApiCallback<R>`（通常以匿名内部类形式），并传递给 `ApiTemplate.execute(callback)`，由后者负责错误处理和响应标准化，最终返回统一的 `ApiResponse<R>`。

```
ApiCallback<R>.call()          →  原始 HTTP 请求（由实现者完成）
ApiTemplate.execute(callback)  →  错误处理 + 响应封装 → ApiResponse<R>
HttpSender.doOkHttp*(...)      →  静态 OkHttp 工具方法（POST/GET，JSON/表单/查询参数）
```

### 核心类说明

| 类 | 角色 |
|---|---|
| `ApiTemplate` | 顶层入口。通过 try/catch 包裹回调执行，返回 `ApiResponse`。 |
| `ApiCallback<R>` | 接口。需实现 `call()`（发起 HTTP 请求）、`getAbility()`（API 标识字符串）、`getTypeReference()`（fastjson2 的 `TypeReference<R>`，用于泛型感知的反序列化）。默认的 `getData()` 方法处理 JSON 解析。 |
| `HttpSender` | 静态 OkHttp 工具类 — `doOkHttpPost` / `doOkHttpGet` 提供多种重载。连接超时 10 秒，读取超时 30 秒。 |
| `ApiResponse<R>` | 标准化响应包装器：`code`、`success`、`result`、`message`。静态工厂方法 `success(R)` 和 `fail(String)`。 |
| `ThirdAbility` | 能力枚举接口 — `getName()` 和 `getUri()`。使用者定义一个枚举来列出其第三方服务的所有端点。 |
| `ApiRequestModel` | 请求 DTO 接口，提供默认的 `toRequestJSON()` / `toRequestMap()` 序列化方法。 |
| `ApiResultModel` | 响应 DTO 标记接口（无方法）。在整个框架中作为 `R` 的泛型上界使用。 |
| `ApiException` | API 调用失败时抛出的非受检异常（继承 `RuntimeException`）。 |
| `ApiServiceTemplate` | 接口契约，定义 `doRequest(ThirdAbility, ApiRequestModel)`。由使用方实现。 |

### 依赖

- **OkHttp 4.9.3** — HTTP 客户端
- **fastjson2 2.0.57** — JSON 序列化/反序列化，`TypeReference` 用于泛型类型捕获
- **Lombok 1.18.38**（provided 作用域）— `@Data`、`@Slf4j`、`@Accessors`

### TypeReference 模式

`ApiCallback.getTypeReference()` 返回 fastjson2 的 `TypeReference<R>`，用于在运行时捕获泛型类型信息。这使得 `getData()` 能够将 JSON 反序列化为正确的具体类型，而无需调用者传递 `Class<R>`。典型用法是通过匿名 `ApiCallback` 子类：

```java
new ApiCallback<SomeResultModel>() {
    public TypeReference<SomeResultModel> getTypeReference() {
        return new TypeReference<SomeResultModel>() {};
    }
}
```

## 已知问题

- **死代码**：`ThirdResultModelConverter.resultConverter()` 在此代码库中未被任何代码调用。
- **无测试** — `src/test` 目录尚不存在。

## Skills

对接指南与多语言文档位于 [`skills/`](skills/)：
- [`CLAUDE.md`](skills/CLAUDE.md) — 本文件的英文版
- [`CLAUDE_zh.md`](skills/CLAUDE_zh.md) — 本文件的中文版
- [`VAN_BRIDGE_INTEGRATION_GUIDE.md`](skills/VAN_BRIDGE_INTEGRATION_GUIDE.md) — 带代码示例的分步快速对接指南
