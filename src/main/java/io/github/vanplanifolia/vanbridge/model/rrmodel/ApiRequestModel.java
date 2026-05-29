package io.github.vanplanifolia.vanbridge.model.rrmodel;


import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

/**
 * 请求参数模型接口。
 * <p>
 * 所有第三方API的请求DTO应实现此接口，以获取默认的序列化能力。
 * 提供将请求对象转换为 fastjson2 {@link JSONObject} 或 {@link Map} 的便捷方法，
 * 用于构造 HTTP 请求体或查询参数。
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * public class SomeApiRequest implements ApiRequestModel {
 *     private String name;
 *     private int page;
 *     // getter / setter ...
 *
 *     // 可直接调用:
 *     // JSONObject json = request.toRequestJSON();
 *     // Map map = request.toRequestMap();
 * }
 * }</pre>
 *
 * @author Van.Planifolia
 * @since 1.0
 */
public interface ApiRequestModel {

    /**
     * 将当前请求对象序列化为 {@link JSONObject}。
     * <p>
     * 内部通过 JSON 字符串往返转换实现，适合请求体为 JSON 格式的 POST 场景。
     * </p>
     *
     * @return 当前对象的 JSONObject 表示
     */
    default JSONObject toRequestJSON() {
        return JSONObject.parseObject(JSONObject.toJSONString(this));
    }

    /**
     * 将当前请求对象序列化为 {@link Map}。
     * <p>
     * 适合作为 GET 请求的查询参数或 POST 表单参数使用。
     * </p>
     *
     * @return 当前对象的 Map 表示
     */
    default Map toRequestMap() {
        return JSONObject.parseObject(JSONObject.toJSONString(this));
    }
}
