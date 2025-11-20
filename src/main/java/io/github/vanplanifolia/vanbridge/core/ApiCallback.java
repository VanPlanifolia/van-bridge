package io.github.vanplanifolia.vanbridge.core;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import io.github.vanplanifolia.vanbridge.exception.ApiException;
import io.github.vanplanifolia.vanbridge.model.ApiResultModel;

public interface ApiCallback<R extends ApiResultModel> {

    /**
     * 执行远程调用
     */
    String call() throws ApiException;

    /**
     * 能力标识
     */
    String getAbility();

    /**
     * 获取泛型类型（由匿名内部类或实现类自动捕获）
     */
    TypeReference<R> getTypeReference();

    /**
     * JSON 转对象
     */
    default R getData(String result) {
        return JSONObject.parseObject(result, getTypeReference());
    }
    /**
     * JSON 转对象
     */
    default R getData(JSONObject resultJSONObject) {
        return resultJSONObject.to(getTypeReference());
    }
}
