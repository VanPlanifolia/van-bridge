package io.github.vanplanifolia.vanbridge.model;


import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

/**
 * @Description
 * @Author Van.Planifolia
 * @Date 2025/10/27
 */

public interface ApiRequestModel {
    default JSONObject toRequestJSON() {
        return JSONObject.parseObject(JSONObject.toJSONString(this));
    }

    default Map toRequestMap() {
        return JSONObject.parseObject(JSONObject.toJSONString(this));
    }
}
