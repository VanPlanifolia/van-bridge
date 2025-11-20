package io.github.vanplanifolia.vanbridge.core;

import com.alibaba.fastjson2.JSONObject;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.github.vanplanifolia.vanbridge.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: HTTP_Util
 * @Author: Planifolia.Van
 * @Date: 2024/12/3 11:46
 */
public class HttpSender {

    private static final Logger log = LoggerFactory.getLogger(HttpSender.class);

    /**
     * Media类型JSON
     */
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    /**
     * 构建OKHttp请求的Client
     */
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * 发送一条OKHTTP的Post请求
     *
     * @param body 请求参数
     * @param url  请求连接
     * @return 结果
     */
    public static String doOkHttpPost(String url, JSONObject body, Map<String, String> headers) {
        log.debug("\n请求Host:{}\n" +
                        "请求Body:{}\n" +
                        "请求header:{}\n",
                url, body, headers);
        RequestBody requestBody = RequestBody.create(body.toJSONString(), JSON);
        Request.Builder requestBuilder = new Request.Builder().url(url).post(requestBody);
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        Request request = requestBuilder.build();

        try (Response response = CLIENT.newCall(request).execute()) {
            log.debug("RAW响应 \nresponse:{}\n,header:{}\n", response, response.headers());
            if (response.isSuccessful() && response.body() != null) {
                String bodyStr = response.body().string();
                log.debug("body:{}", bodyStr);
                return bodyStr;
            } else {
                throw new IOException(String.format("远端服务异常!\n%s", response.message()));
            }
        } catch (Exception e) {
            log.debug("OkHTTP-POST-JSON 请求发送失败，错误消息:{}", e.getMessage());
            throw new ApiException(e.getMessage());
        }
    }

    /**
     * 发送OkHttpPost请求 表单版
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求表单数据
     * @return 响应结果
     */
    public static String doOkHttpPost(String url, Map<String, String> params, Map<String, String> headers) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null) {
            params.forEach(formBodyBuilder::add);
        }

        RequestBody body = formBodyBuilder.build();

        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        Request request = requestBuilder.build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new ApiException(String.format("code: %s 远端服务host: %s 异常，请联系服务提供商。", response.code(), url));
            }
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    /**
     * 发送OkHttpPost请求 表单版
     *
     * @param url    请求URL
     * @param params 请求表单数据
     * @return 响应结果
     */
    public static String doOkHttpPost(String url, Map params) throws ApiException {
        Map<String, String> stringMap = new HashMap<>();
        params.forEach((key, value) -> stringMap.put(key.toString(), value.toString()));
        return doOkHttpPost(url, stringMap, null);
    }

    /**
     * 发送 GET 请求
     *
     * @param url    请求地址
     * @param params URL 参数
     * @return 响应结果
     */
    public static String doOkHttpGet(String url, Map params) {
        Map<String, String> stringMap = new HashMap<>();
        params.forEach((key, value) -> stringMap.put(key.toString(), value.toString()));
        return doOkHttpGet(url, stringMap, null);
    }

    /**
     * 发送 GET 请求
     *
     * @param url     请求地址
     * @param headers 自定义请求头
     * @param params  URL 参数
     * @return 响应结果
     */
    public static String doOkHttpGet(String url, Map<String, String> params, Map<String, String> headers) {
        // 拼接 URL 参数
        if (params != null && !params.isEmpty()) {
            StringBuilder urlBuilder = new StringBuilder(url).append("?");
            params.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
            url = urlBuilder.substring(0, urlBuilder.length() - 1); // 去掉最后的 &
        }

        // 构建请求
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader); // 添加请求头
        }

        Request request = requestBuilder.get().build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                log.error("请求url\n:{}", url);
                throw new RuntimeException(String.format("code: %s 远端服务host: %s 异常，请联系服务提供商。", response.code(), url));
            }
        } catch (IOException e) {
            log.debug("OkHTTP-GET-FORM 请求发送失败，错误消息:{}", e.getMessage());
            throw new RuntimeException("请求失败！");
        }
    }

    /**
     * 发送 GET 请求
     *
     * @param url     请求地址
     * @param headers 自定义请求头
     * @param params  URL 参数
     * @return 响应结果
     */
    public static String doOkHttpGet(String url, JSONObject params, Map<String, String> headers) {
        return doOkHttpGet(url, params.toJavaObject(Map.class), headers);
    }


    /**
     * 解析返回值
     *
     * @param responseBody 响应结果
     * @return 解析成字符串
     */
    private static String resolver(ResponseBody responseBody) {
        InputStream is = null;
        String result;
        try {
            is = responseBody.byteStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String body;
            StringBuilder sb = new StringBuilder();
            while ((body = br.readLine()) != null) {
                sb.append(body);
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                log.error("解析OkHttp响应-关闭资源异常！");
            }
        }
        return result;
    }
}
