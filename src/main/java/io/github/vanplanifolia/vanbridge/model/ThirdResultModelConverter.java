package io.github.vanplanifolia.vanbridge.model;


import com.alibaba.fastjson2.JSONObject;
import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel;

/**
 * 第三方响应结果转换器。
 * <p>
 * 提供将第三方API返回的原始JSON字符串反序列化为指定的 {@link ApiResultModel} 实现类的工具方法。
 * 内部使用 fastjson2 进行解析。
 * </p>
 *
 * @author Van.Planifolia
 * @since 1.0
 */
public class ThirdResultModelConverter {

    /**
     * 将JSON字符串转换为指定的响应模型对象。
     *
     * @param json  第三方API返回的原始JSON字符串
     * @param clazz 目标响应模型类的Class对象
     * @param <R>   响应模型类型，必须实现 {@link ApiResultModel}
     * @return 反序列化后的响应模型实例
     */
    public static <R extends ApiResultModel> R resultConverter(String json, Class<R> clazz) {
        return JSONObject.parseObject(json, clazz);
    }
}
