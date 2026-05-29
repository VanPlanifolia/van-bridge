package io.github.vanplanifolia.vanbridge.exception;

/**
 * API调用异常，作为框架内所有远程调用失败时的统一非受检异常。
 * <p>
 * 当 HttpSender 底层请求失败或响应状态码异常时抛出，
 * 由 {@link io.github.vanplanifolia.vanbridge.core.ApiTemplate#execute} 统一捕获并转换为 ApiResponse.fail。
 * </p>
 *
 * @author Planifolia.Van
 * @since 1.0
 */
public class ApiException extends RuntimeException {

    /**
     * 通过错误消息构造异常。
     *
     * @param message 错误描述信息
     */
    public ApiException(String message) {
        super(message);
    }

    /**
     * 通过错误消息与原始异常构造异常，保留异常链。
     *
     * @param message 错误描述信息
     * @param cause   原始异常
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
