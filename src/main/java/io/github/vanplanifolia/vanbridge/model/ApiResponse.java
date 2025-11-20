package io.github.vanplanifolia.vanbridge.model;

import kotlin.Result;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @Author: Planifolia.Van
 * @Date: 2025/4/10 15:28
 */
@Data
@Accessors(chain = true)
public class ApiResponse<R extends ApiResultModel> {

    private int code;
    /**
     * 响应码
     */
    private Boolean success;
    /**
     * 响应内容
     */
    private R result;
    /**
     * 失败结果
     */
    private String message;

    /**
     * 是否成功
     *
     * @return 成功标记
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * 失败
     *
     * @param message 失败原因
     * @return 失败对象
     */
    public static <R extends ApiResultModel> ApiResponse<R> fail(String message) {
        return new ApiResponse<R>().setSuccess(Boolean.FALSE).setMessage(message).setCode(500);
    }

    /**
     * 成功
     *
     * @param result 结果
     * @return 成功对象
     */
    public static <R extends ApiResultModel> ApiResponse<R> success(R result) {
        return new ApiResponse<R>().setResult(result).setSuccess(Boolean.TRUE).setCode(200);
    }



}
