package io.github.vanplanifolia.vanbridge.core;


import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiRequestModel;

/**
 * 第三方服务请求模板接口。
 * <p>
 * 定义统一的请求入口，使用方针对每个第三方服务实现此接口，
 * 根据传入的 {@link ThirdAbility} 枚举动态路由到具体的API端点并发起调用。
 * 通常与 {@link io.github.vanplanifolia.vanbridge.core.ApiCallback} 配合使用，
 * 在 {@code doRequest} 内部构造并返回回调的 {@code call()} 结果。
 * </p>
 *
 * @author Van.Planifolia
 * @since 1.0
 */
public interface ApiServiceTemplate {

    /**
     * 执行第三方API请求。
     *
     * @param thirdAbility    API能力枚举，标识要调用的具体端点
     * @param apiRequestModel 请求参数模型
     * @return 远程服务返回的原始JSON字符串
     */
    String doRequest(ThirdAbility thirdAbility, ApiRequestModel apiRequestModel);
}
