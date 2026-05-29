package io.github.vanplanifolia.vanbridge.model.rrmodel;

/**
 * 响应结果标记接口。
 * <p>
 * 所有第三方API的响应DTO必须实现此接口，作为框架泛型系统的类型上界。
 * 此接口不定义任何方法，仅用于在 {@link io.github.vanplanifolia.vanbridge.core.ApiCallback}、
 * {@link io.github.vanplanifolia.vanbridge.model.ApiResponse} 等泛型类中约束类型参数，
 * 确保只有响应模型可被框架统一处理。
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * public class SomeApiResult implements ApiResultModel {
 *     private String field1;
 *     private int field2;
 *     // getter / setter ...
 * }
 * }</pre>
 *
 * @author Van.Planifolia
 * @since 1.0
 * @see io.github.vanplanifolia.vanbridge.core.ApiCallback
 * @see io.github.vanplanifolia.vanbridge.model.ApiResponse
 */
public interface ApiResultModel {
}
