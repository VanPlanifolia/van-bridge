# van-bridge 快速对接指南

## 概述

van-bridge 是一个轻量级的第三方 REST API 调用抽象层。你只需要定义请求/响应模型和 API 枚举，框架会处理异常捕获、JSON 解析和响应标准化。

## 1. 引入依赖

```xml
<dependency>
    <groupId>io.github.vanplanifolia</groupId>
    <artifactId>van-bridge</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 2. 三步对接

### 第一步：定义 API 能力枚举

实现 `ThirdAbility` 接口，列出你要对接的第三方服务的所有端点。

```java
public enum SomeApi implements ThirdAbility {

    GET_USER_INFO("getUserInfo", "/api/v1/user/info"),
    SEND_MESSAGE("sendMessage", "/api/v1/message/send");

    private final String name;
    private final String uri;

    SomeApi(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getUri() { return uri; }
}
```

### 第二步：定义请求/响应模型

**请求模型** — 实现 `ApiRequestModel`，自动获得 `toRequestJSON()` 和 `toRequestMap()` 序列化能力。

```java
@Data
public class GetUserInfoRequest implements ApiRequestModel {
    private String userId;
    private int page;
    private int pageSize;
}
```

**响应模型** — 实现 `ApiResultModel`（标记接口），字段与第三方返回的 JSON 结构对应。

```java
@Data
public class GetUserInfoResult implements ApiResultModel {
    private String userId;
    private String userName;
    private String email;
}
```

### 第三步：调用

通过匿名 `ApiCallback` 发起调用，交给 `ApiTemplate.execute()` 统一处理。

```java
public ApiResponse<GetUserInfoResult> getUserInfo(String userId) {
    String baseUrl = "https://api.example.com";

    return ApiTemplate.execute(new ApiCallback<GetUserInfoResult>() {

        @Override
        public String call() {
            GetUserInfoRequest req = new GetUserInfoRequest();
            req.setUserId(userId);
            // 使用 JSON body 发送 POST
            return HttpSender.doOkHttpPost(
                baseUrl + SomeApi.GET_USER_INFO.getUri(),
                req.toRequestJSON(),
                null  // 不需要自定义 header 时传 null
            );
        }

        @Override
        public String getAbility() {
            return SomeApi.GET_USER_INFO.getName();
        }

        @Override
        public TypeReference<GetUserInfoResult> getTypeReference() {
            return new TypeReference<GetUserInfoResult>() {};
        }
    });
}

// 调用方代码
ApiResponse<GetUserInfoResult> response = userService.getUserInfo("123");
if (response.isSuccess()) {
    GetUserInfoResult user = response.getResult();
} else {
    log.error("调用失败：{}", response.getMessage());
}
```

## 3. 使用 ApiServiceTemplate 模式（推荐多端点服务）

当同一个第三方服务有多个端点时，建议实现 `ApiServiceTemplate` 做统一路由。

```java
public class SomeApiService implements ApiServiceTemplate {

    private static final String BASE_URL = "https://api.example.com";

    @Override
    public String doRequest(ThirdAbility ability, ApiRequestModel requestModel) {
        String url = BASE_URL + ability.getUri();
        return HttpSender.doOkHttpPost(url, requestModel.toRequestJSON(), buildHeaders());
    }

    private Map<String, String> buildHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getToken());
        headers.put("Content-Type", "application/json");
        return headers;
    }

    // --- 业务方法 ---

    public ApiResponse<GetUserInfoResult> getUserInfo(String userId) {
        GetUserInfoRequest req = buildRequest(userId);
        return executeWithCallback(SomeApi.GET_USER_INFO, req, new TypeReference<GetUserInfoResult>() {});
    }

    public ApiResponse<SendMessageResult> sendMessage(String to, String content) {
        SendMessageRequest req = buildMessageRequest(to, content);
        return executeWithCallback(SomeApi.SEND_MESSAGE, req, new TypeReference<SendMessageResult>() {});
    }

    // 通用执行器
    private <R extends ApiResultModel> ApiResponse<R> executeWithCallback(
            SomeApi api, ApiRequestModel req, TypeReference<R> typeRef) {
        return ApiTemplate.execute(new ApiCallback<R>() {
            @Override
            public String call() {
                return doRequest(api, req);
            }
            @Override
            public String getAbility() {
                return api.getName();
            }
            @Override
            public TypeReference<R> getTypeReference() {
                return typeRef;
            }
        });
    }
}
```

## 4. 处理列表/分页响应

框架泛型约束 `R extends ApiResultModel`，不能直接写 `ApiCallback<List<XxxResult>>`。
解决方案：定义通用分页包装类。

```java
@Data
public class PageResult<T> implements ApiResultModel {

    @JSONField(name = "total_pages")
    private Integer totalPages;

    @JSONField(name = "page_no")
    private Integer pageNo;

    @JSONField(name = "total_count")
    private Integer totalCount;

    private List<T> list;
}
```

### 4.1 具体类型调用

```java
public ApiResponse<PageResult<UserInfoResult>> listUsers() {
    return ApiTemplate.execute(new ApiCallback<PageResult<UserInfoResult>>() {
        @Override
        public String call() {
            return HttpSender.doOkHttpGet(BASE_URL + "/users", params, null);
        }
        @Override
        public String getAbility() { return "listUsers"; }
        @Override
        public TypeReference<PageResult<UserInfoResult>> getTypeReference() {
            return new TypeReference<PageResult<UserInfoResult>>() {};
        }
    });
}

// 调用方
ApiResponse<PageResult<UserInfoResult>> resp = service.listUsers();
if (resp.isSuccess()) {
    PageResult<UserInfoResult> page = resp.getResult();
    page.getTotalPages();   // 总页数
    page.getTotalCount();   // 总条数
    page.getList();         // List<UserInfoResult>
}
```

### 4.2 泛型复用：通过 Class<T> 解析内层类型

当多个分页接口元素类型不同时，用泛型方法避免重复写匿名类：

```java
private <T> ApiResponse<PageResult<T>> executePageRequest(
        SomeApi api, ApiRequestModel request, Class<T> itemType) {

    return ApiTemplate.execute(new ApiCallback<PageResult<T>>() {
        @Override
        public String call() {
            return request != null
                    ? doRequest(api, request)
                    : HttpSender.doOkHttpGet(BASE_URL + api.getUri(), null, null);
        }
        @Override
        public String getAbility() { return api.getName(); }
        @Override
        public TypeReference<PageResult<T>> getTypeReference() {
            // itemType 传入 TypeReference 构造器，fastjson2 据此解析 PageResult<T> 的内层泛型
            return new TypeReference<PageResult<T>>(itemType) {};
        }
    });
}

// 使用：每种分页接口一行搞定
public ApiResponse<PageResult<UserInfoResult>> listUsers() {
    return executePageRequest(SomeApi.LIST_USERS, null, UserInfoResult.class);
}
public ApiResponse<PageResult<OrderResult>> listOrders() {
    return executePageRequest(SomeApi.LIST_ORDERS, null, OrderResult.class);
}
```

## 5. HttpSender 速查

| 场景 | 方法 |
|---|---|
| POST + JSON 请求体 | `doOkHttpPost(url, jsonObject, headers)` |
| POST + 表单编码 | `doOkHttpPost(url, mapParams, headers)` |
| GET + 查询参数 (Map) | `doOkHttpGet(url, mapParams, headers)` |
| GET + 查询参数 (JSONObject) | `doOkHttpGet(url, jsonParams, headers)` |

- 连接超时：10 秒，读取超时：30 秒
- `headers` 参数可为 `null`，表示无自定义请求头
- 所有方法在异常时抛出 `ApiException`（`ApiTemplate.execute` 会自动捕获）

## 6. 错误处理

`ApiTemplate.execute()` 永远不会抛出异常——它把所有失败情况都装进 `ApiResponse`：

- **远程调用失败** → `response.isSuccess() == false`，`response.getMessage()` 包含 "远程调用接口：xxx 异常"
- **JSON 解析失败** → `response.isSuccess() == false`，`response.getMessage()` 包含 "结果解析异常"
- **成功** → `response.isSuccess() == true`，`response.getResult()` 获取数据

## 7. TypeReference 要点

`ApiCallback.getTypeReference()` 必须返回 `TypeReference` 的**匿名子类**，利用 JVM 对匿名类的泛型保留机制：

```java
// 正确：匿名子类保留泛型信息
new TypeReference<GetUserInfoResult>() {};

// 正确：带参数的泛型类型，通过构造器传入 itemType 解析内层泛型
new TypeReference<PageResult<UserInfoResult>>(UserInfoResult.class) {};

// 错误：直接 new 会丢失泛型，导致反序列化失败
new TypeReference<GetUserInfoResult>();  // 不要这样做
```
