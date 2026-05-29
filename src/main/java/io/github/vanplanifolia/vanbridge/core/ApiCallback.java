package io.github.vanplanifolia.vanbridge.core;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import io.github.vanplanifolia.vanbridge.exception.ApiException;
import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel;

/**
 * API回调接口，封装单次API调用的完整生命周期。
 * <p>
 * 这是框架的核心抽象——使用方通过实现此接口（通常使用匿名内部类）来定义：
 * <ol>
 *   <li><b>如何发起HTTP请求</b> — {@link #call()}</li>
 *   <li><b>调用的是哪个API</b> — {@link #getAbility()}</li>
 *   <li><b>如何反序列化响应</b> — {@link #getTypeReference()}</li>
 * </ol>
 * 完成后将回调实例传入 {@link io.github.vanplanifolia.vanbridge.core.ApiTemplate#execute(ApiCallback)}
 * 即可获得标准化的 {@link io.github.vanplanifolia.vanbridge.model.ApiResponse}。
 * </p>
 *
 * <h3>关键设计：TypeReference 泛型捕获</h3>
 * <p>
 * 通过 {@link #getTypeReference()} 返回 fastjson2 的 {@link TypeReference} 匿名子类，
 * 利用 JVM 的泛型擦除保留机制在运行时获取具体的泛型类型，
 * 从而 {@link #getData(String)} 无需调用者传入 {@code Class<R>} 即可正确反序列化。
 * </p>
 *
 * @param <R> 响应数据类型，必须实现 {@link ApiResultModel}
 * @author Planifolia.Van
 * @since 1.0
 * @see io.github.vanplanifolia.vanbridge.core.ApiTemplate#execute(ApiCallback)
 */
public interface ApiCallback<R extends ApiResultModel> {

    /**
     * 执行远程调用并返回原始响应字符串。
     * <p>
     * 实现者在此方法中通过 {@link HttpSender} 或自定义方式发起HTTP请求，
     * 返回第三方服务响应的原始JSON字符串。框架会接管后续的解析和异常处理。
     * </p>
     *
     * @return 远程服务返回的原始JSON字符串
     * @throws ApiException 当HTTP调用失败时抛出
     */
    String call() throws ApiException;

    /**
     * 返回当前调用的API能力标识字符串。
     * <p>
     * 该标识用于日志输出和异常信息中定位具体是哪个API出错，
     * 通常返回 {@link ThirdAbility#getName()} 的值。
     * </p>
     *
     * @return API能力标识，如 "getUserInfo"
     */
    String getAbility();

    /**
     * 返回用于反序列化的泛型类型引用。
     * <p>
     * 必须通过创建 {@link TypeReference} 的匿名子类来返回，以保留运行时的泛型信息。
     * </p>
     *
     * <pre>{@code
     * new TypeReference<SomeResult>() {};  // 正确：匿名子类保留泛型信息
     * }</pre>
     *
     * @return 捕获了具体泛型类型的 TypeReference 实例
     */
    TypeReference<R> getTypeReference();

    /**
     * 将JSON字符串解析为响应模型对象。
     *
     * @param result 第三方API返回的JSON字符串
     * @return 反序列化后的响应模型对象
     */
    default R getData(String result) {
        return JSONObject.parseObject(result, getTypeReference());
    }

    /**
     * 将 {@link JSONObject} 转换为响应模型对象。
     *
     * @param resultJSONObject fastjson2 的 JSONObject 实例
     * @return 转换后的响应模型对象
     */
    default R getData(JSONObject resultJSONObject) {
        return resultJSONObject.to(getTypeReference());
    }
}
