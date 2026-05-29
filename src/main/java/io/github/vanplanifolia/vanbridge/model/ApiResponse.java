package io.github.vanplanifolia.vanbridge.model;

import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 标准化API响应包装器。
 * <p>
 * 作为框架与调用方之间的统一响应格式，封装了远程调用的三种可能结果：
 * <ul>
 *   <li><b>成功</b> — code=200，success=true，result 中包含业务数据</li>
 *   <li><b>远程调用失败</b> — code=500，success=false，message 中包含异常原因</li>
 *   <li><b>JSON解析失败</b> — code=500，success=false，message 中包含解析错误信息</li>
 * </ul>
 * 使用 Lombok {@code @Accessors(chain = true)} 支持链式调用。
 * </p>
 *
 * @param <R> 响应数据类型，必须实现 {@link ApiResultModel}
 * @author Planifolia.Van
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class ApiResponse<R extends ApiResultModel> {

    /** HTTP 风格的状态码，200 表示成功，500 表示失败 */
    private int code;

    /** 成功标记，true 表示调用成功 */
    private Boolean success;

    /** 成功时的业务响应数据 */
    private R result;

    /** 失败时的错误描述信息 */
    private String message;

    /**
     * 判断本次调用是否成功。
     *
     * @return true 表示成功，false 表示失败
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * 构造一个失败响应。
     *
     * @param <R>     响应数据类型
     * @param message 失败原因描述
     * @return code=500、success=false 的响应对象
     */
    public static <R extends ApiResultModel> ApiResponse<R> fail(String message) {
        return new ApiResponse<R>().setSuccess(Boolean.FALSE).setMessage(message).setCode(500);
    }

    /**
     * 构造一个成功响应。
     *
     * @param <R>    响应数据类型
     * @param result 业务响应数据
     * @return code=200、success=true 的响应对象
     */
    public static <R extends ApiResultModel> ApiResponse<R> success(R result) {
        return new ApiResponse<R>().setResult(result).setSuccess(Boolean.TRUE).setCode(200);
    }

}
