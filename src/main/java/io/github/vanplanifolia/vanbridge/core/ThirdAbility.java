package io.github.vanplanifolia.vanbridge.core;

/**
 * 第三方API能力定义接口。
 * <p>
 * 使用者需通过 <b>枚举</b> 实现此接口，以声明式地列出某个第三方服务的所有API端点。
 * 每个枚举常量代表一个具体的API能力，包含其名称标识和URI路径。
 * 配合 {@link io.github.vanplanifolia.vanbridge.core.ApiCallback#getAbility()} 使用，
 * 可在日志和异常信息中精确定位是哪个API调用失败。
 * </p>
 *
 * <h3>枚举定义示例</h3>
 * <pre>{@code
 * public enum SomeThirdApi implements ThirdAbility {
 *     GET_USER_INFO("getUserInfo", "/api/v1/user/info"),
 *     SEND_MESSAGE("sendMessage", "/api/v1/message/send");
 *
 *     private final String name;
 *     private final String uri;
 *
 *     SomeThirdApi(String name, String uri) {
 *         this.name = name;
 *         this.uri = uri;
 *     }
 *
 *     public String getName() { return name; }
 *     public String getUri() { return uri; }
 * }
 * }</pre>
 *
 * @author Van.Planifolia
 * @since 1.0
 */
public interface ThirdAbility {

    /**
     * 返回该API能力的名称标识。
     * <p>
     * 约定使用枚举常量的 {@code name()} 或自定义的逻辑名称，
     * 用于日志输出和异常信息中的API标识。
     * </p>
     *
     * @return API名称
     */
    String getName();

    /**
     * 返回该API的URI路径。
     * <p>
     * 通常为相对路径（如 {@code /api/v1/user/info}），
     * 由调用方在 {@link io.github.vanplanifolia.vanbridge.core.ApiCallback#call()} 中
     * 与基础域名拼接为完整的请求URL。
     * </p>
     *
     * @return API的URI路径
     */
    String getUri();
}
