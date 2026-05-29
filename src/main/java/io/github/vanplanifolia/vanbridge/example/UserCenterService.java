package io.github.vanplanifolia.vanbridge.example;

import com.alibaba.fastjson2.TypeReference;
import io.github.vanplanifolia.vanbridge.core.*;
import io.github.vanplanifolia.vanbridge.model.ApiResponse;
import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiRequestModel;
import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel;

/**
 * 示例：用户中心服务 —— 推荐的多端点服务对接模式。
 * <p>
 * 实现 ApiServiceTemplate 后，通过 {@link #doRequest(ThirdAbility, ApiRequestModel)} 统一管理
 * 所有API调用的公共逻辑（如域名拼接、鉴权Header注入），各业务方法只需关注参数构造和类型指定。
 * </p>
 */
public class UserCenterService implements ApiServiceTemplate {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    /**
     * 统一请求入口：拼接完整URL + 请求体序列化 + 发起HTTP请求。
     * 所有业务方法最终都委托到此方法执行实际的HTTP调用。
     */
    @Override
    public String doRequest(ThirdAbility ability, ApiRequestModel requestModel) {
        String fullUrl = BASE_URL + ability.getUri();
        // POST 场景使用 requestModel.toRequestJSON() 作为请求体
        return HttpSender.doOkHttpPost(fullUrl, requestModel.toRequestJSON(), null);
    }

    // ==================== 业务方法 ====================

    /**
     * 根据ID获取单个用户（GET请求）。
     */
    public ApiResponse<UserInfoResult> getUserById(Long userId) {
        String path = UserCenterApi.GET_USER_BY_ID.getUri().replace("{id}", String.valueOf(userId));

        return ApiTemplate.execute(new ApiCallback<UserInfoResult>() {
            @Override
            public String call() {
                return HttpSender.doOkHttpGet(BASE_URL + path, (java.util.Map<String, String>) null, null);
            }

            @Override
            public String getAbility() {
                return UserCenterApi.GET_USER_BY_ID.getName();
            }

            @Override
            public TypeReference<UserInfoResult> getTypeReference() {
                return new TypeReference<UserInfoResult>() {};
            }
        });
    }

    /**
     * 创建新用户（POST请求 —— 使用 ApiServiceTemplate 的 doRequest）。
     */
    public ApiResponse<UserInfoResult> createUser(CreateUserRequest request) {
        return ApiTemplate.execute(new ApiCallback<UserInfoResult>() {
            @Override
            public String call() {
                return doRequest(UserCenterApi.CREATE_USER, request);
            }

            @Override
            public String getAbility() {
                return UserCenterApi.CREATE_USER.getName();
            }

            @Override
            public TypeReference<UserInfoResult> getTypeReference() {
                return new TypeReference<UserInfoResult>() {};
            }
        });
    }

    /**
     * 获取用户分页列表（GET请求 —— 演示列表响应处理）。
     * <p>
     * 由于框架泛型约束 {@code R extends ApiResultModel}，不能直接将
     * {@code List<UserInfoResult>} 作为泛型参数。
     * 因此使用通用分页包装类 {@link PageResult}{@code <UserInfoResult>} 来承载列表数据。
     * </p>
     */
    public ApiResponse<PageResult<UserInfoResult>> listUsers() {
        return executePageRequest(UserCenterApi.LIST_USERS, null, UserInfoResult.class);
    }

    // ==================== 泛型复用：避免每次手写匿名类 ====================

    /**
     * 通用分页请求执行器。
     * <p>
     * 通过将 {@code Class<T>} 传入 {@link TypeReference} 的构造器，
     * fastjson2 可正确解析 {@code PageResult<T>} 中的泛型参数 T。
     * 这样每种分页接口只需一行调用，无需为每种元素类型重复写匿名 ApiCallback。
     * </p>
     *
     * @param api      API 能力枚举
     * @param request  请求参数（GET 场景可为 null）
     * @param itemType 列表元素类型的 Class 对象
     * @param <T>      列表元素类型
     * @return 包装了分页结果的标准化响应
     */
    private <T> ApiResponse<PageResult<T>> executePageRequest(
            UserCenterApi api, ApiRequestModel request, Class<T> itemType) {

        return ApiTemplate.execute(new ApiCallback<PageResult<T>>() {
            @Override
            public String call() {
                if (request != null) {
                    return doRequest(api, request);
                }
                return HttpSender.doOkHttpGet(BASE_URL + api.getUri(),
                        (java.util.Map<String, String>) null, null);
            }

            @Override
            public String getAbility() {
                return api.getName();
            }

            @Override
            public TypeReference<PageResult<T>> getTypeReference() {
                // 将 itemType 传入构造器以解析 PageResult<T> 的内层泛型
                return new TypeReference<PageResult<T>>(itemType) {};
            }
        });
    }
}
