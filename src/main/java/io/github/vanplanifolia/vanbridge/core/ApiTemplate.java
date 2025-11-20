package io.github.vanplanifolia.vanbridge.core;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.github.vanplanifolia.vanbridge.exception.ApiException;
import io.github.vanplanifolia.vanbridge.model.ApiResponse;
import io.github.vanplanifolia.vanbridge.model.ApiResultModel;

/**
 * @Description:
 * @Author: Planifolia.Van
 * @Date: 2025/4/10 15:38
 */
public class ApiTemplate {


    private static final Logger log = LoggerFactory.getLogger(ApiTemplate.class);

    /**
     * 执行腾讯请求
     *
     * @param callback 如何执行请求
     * @param <R>      响应通用模板
     * @return 通用响应结果
     */
    public static <R extends ApiResultModel> ApiResponse<R> execute(ApiCallback<R> callback) throws ApiException {
        String call;
        try {
            call = callback.call();
        } catch (ApiException apiException) {
            log.error("远程调用-异常信息:{}", apiException.getMessage());
            return ApiResponse.fail(String.format("远程调用接口：%s 异常,异常原因:%s", callback.getAbility(),apiException.getMessage()));
        }
        R resultModel;
        try {
            resultModel = callback.getData(call);
        } catch (Exception e) {
            log.error("解析json-异常信息:{}\n原始内容:{}", e.getMessage(), call);
            return ApiResponse.fail(String.format("接口：%s 结果解析异常", callback.getAbility()));
        }
        return ApiResponse.success(resultModel);
    }
}
