package io.github.vanplanifolia.vanbridge.core;


import io.github.vanplanifolia.vanbridge.model.ApiRequestModel;

/**
 * @Description 三方模板请求Service
 * @Author Van.Planifolia
 * @Date 2025/10/31
 */
public interface ApiServiceTemplate {


    /**
     * 亲求模板方法
     *
     * @param thirdAbility API的enum
     * @param apiRequestModel   请求参数
     * @return 构建结果
     */
    String doRequest(ThirdAbility thirdAbility, ApiRequestModel apiRequestModel);
}
