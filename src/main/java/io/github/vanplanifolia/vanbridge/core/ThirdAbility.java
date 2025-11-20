package io.github.vanplanifolia.vanbridge.core;

/**
 * @Description 可拓展的三方API能力定义
 * @Author Van.Planifolia
 * @Date 2025/10/31
 */
public interface ThirdAbility {

    /**
     * 约定枚举的API名称为name
     *
     * @return API NAME
     */
    String getName();

    /**
     * 约定枚举的API URI 为uri
     *
     * @return API uri
     */
    String getUri();
}
