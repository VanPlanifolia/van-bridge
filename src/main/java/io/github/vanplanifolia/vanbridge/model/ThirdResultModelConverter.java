package io.github.vanplanifolia.vanbridge.model;


import com.alibaba.fastjson2.JSONObject;

/**
 * @Description 定义第三方Result
 * @Author Van.Planifolia
 * @Date 2025/10/27
 */
public class ThirdResultModelConverter {

    /**
     * 默认构造器
     *
     * @param json 传参
     */
    public static <R extends ApiResultModel> R resultConverter(String json, Class<R> clazz) {
        return JSONObject.parseObject(json, clazz);
    }
}
