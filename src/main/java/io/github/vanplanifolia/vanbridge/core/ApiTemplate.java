package io.github.vanplanifolia.vanbridge.core;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.github.vanplanifolia.vanbridge.exception.ApiException;
import io.github.vanplanifolia.vanbridge.model.ApiResponse;
import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel;

/**
 * API请求模板——框架的顶层入口。
 * <p>
 * 提供唯一的静态方法 {@link #execute(ApiCallback)}，封装了远程调用的完整生命周期：
 * <ol>
 *   <li>通过 {@link ApiCallback#call()} 执行远程调用</li>
 *   <li>捕获 {@link io.github.vanplanifolia.vanbridge.exception.ApiException} 并转换为失败响应</li>
 *   <li>通过 {@link ApiCallback#getData(String)} 解析响应JSON</li>
 *   <li>捕获JSON解析异常并转换为失败响应</li>
 *   <li>返回标准化的 {@link io.github.vanplanifolia.vanbridge.model.ApiResponse}</li>
 * </ol>
 * 调用方无需自行处理异常或手动构造响应对象。
 * </p>
 *
 * @author Planifolia.Van
 * @since 1.0
 */
public class ApiTemplate {

    private static final Logger log = LoggerFactory.getLogger(ApiTemplate.class);

    /**
     * 执行一次API调用并返回标准化响应。
     * <p>
     * 此方法串联了 "发起调用 → 异常处理 → JSON解析 → 响应封装" 的完整流程。
     * 任何环节的失败都会被优雅地转换为 {@link io.github.vanplanifolia.vanbridge.model.ApiResponse#fail(String)}
     * 而非向上抛出异常，确保调用方始终获得一个可安全处理的响应对象。
     * </p>
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * ApiResponse<SomeResult> response = ApiTemplate.execute(new ApiCallback<SomeResult>() {
     *     public String call() {
     *         return HttpSender.doOkHttpPost("https://api.example.com/user", params, headers);
     *     }
     *     public String getAbility() { return "getUserInfo"; }
     *     public TypeReference<SomeResult> getTypeReference() {
     *         return new TypeReference<SomeResult>() {};
     *     }
     * });
     * if (response.isSuccess()) {
     *     SomeResult data = response.getResult();
     * } else {
     *     log.error(response.getMessage());
     * }
     * }</pre>
     *
     * @param <R>      响应数据类型，必须实现 {@link io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel}
     * @param callback 定义了如何调用、如何解析的回调实例
     * @return 标准化的API响应，成功或失败均由此对象承载
     */
    public static <R extends ApiResultModel> ApiResponse<R> execute(ApiCallback<R> callback) {
        String call;
        try {
            call = callback.call();
        } catch (ApiException apiException) {
            log.error("远程调用-异常信息:{}", apiException.getMessage());
            return ApiResponse.fail(String.format("远程调用接口：%s 异常,异常原因:%s", callback.getAbility(), apiException.getMessage()));
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
