package io.github.vanplanifolia.vanbridge.example;

import io.github.vanplanifolia.vanbridge.core.ThirdAbility;

/**
 * 示例：用户中心API能力枚举。
 * 每个枚举常量对应一个第三方服务的API端点。
 */
public enum UserCenterApi implements ThirdAbility {

    /** 根据ID获取用户信息 */
    GET_USER_BY_ID("getUserById", "/users/{id}"),

    /** 获取用户列表 */
    LIST_USERS("listUsers", "/users"),

    /** 创建新用户 */
    CREATE_USER("createUser", "/users");

    private final String name;
    private final String uri;

    UserCenterApi(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUri() {
        return uri;
    }
}
