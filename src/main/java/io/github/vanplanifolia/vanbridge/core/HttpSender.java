package io.github.vanplanifolia.vanbridge.core;

import com.alibaba.fastjson2.JSONObject;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.github.vanplanifolia.vanbridge.exception.ApiException;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 请求发送器，封装 OkHttp 的底层操作。
 * <p>
 * 提供一系列静态方法用于执行 HTTP POST/GET 请求，支持：
 * <ul>
 *   <li>JSON 请求体（POST）</li>
 *   <li>表单编码请求体（POST）</li>
 *   <li>查询参数拼接（GET）</li>
 *   <li>自定义请求头</li>
 * </ul>
 * 所有方法在底层网络异常时均抛出 {@link ApiException}（非受检），
 * 调用方可自行决定处理或交由 {@link ApiTemplate#execute} 统一兜底。
 * </p>
 *
 * <h3>超时配置</h3>
 * <ul>
 *   <li>连接超时：10 秒</li>
 *   <li>读取超时：30 秒</li>
 * </ul>
 *
 * @author Planifolia.Van
 * @since 1.0
 */
public class HttpSender {

    private static final Logger log = LoggerFactory.getLogger(HttpSender.class);

    /** JSON 媒体类型（application/json; charset=utf-8） */
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /** 全局复用的 OkHttp 客户端实例 */
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * 发送 JSON 格式的 POST 请求。
     *
     * @param url     请求URL
     * @param body    请求体（JSONObject，将序列化为JSON字符串）
     * @param headers 自定义请求头，可为 null
     * @return 响应体字符串
     * @throws ApiException 网络异常或服务端返回非成功状态码时抛出
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
     * 发送表单编码的 POST 请求。
     * <p>
     * 请求体以 {@code application/x-www-form-urlencoded} 格式编码。
     * </p>
     *
     * @param url     请求URL
     * @param params  表单参数
     * @param headers 自定义请求头，可为 null
     * @return 响应体字符串
     * @throws ApiException 网络异常或服务端返回非成功状态码时抛出
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
     * 发送表单编码的 POST 请求（无自定义请求头，参数类型宽松）。
     * <p>
     * 这是一个便捷重载，接受原始 {@link Map} 并将所有值转为字符串后发起调用。
     * </p>
     *
     * @param url    请求URL
     * @param params 表单参数（原始 Map，key/value 将自动调用 toString）
     * @return 响应体字符串
     * @throws ApiException 网络异常或服务端返回非成功状态码时抛出
     */
    public static String doOkHttpPost(String url, Map params) throws ApiException {
        Map<String, String> stringMap = new HashMap<>();
        params.forEach((key, value) -> stringMap.put(key.toString(), value.toString()));
        return doOkHttpPost(url, stringMap, null);
    }

    /**
     * 发送 GET 请求（无自定义请求头，参数类型宽松）。
     * <p>
     * 便捷重载，接受原始 {@link Map} 并将所有值转为字符串后拼接到URL查询参数中。
     * 注意：参数值不会进行URL编码。
     * </p>
     *
     * @param url    请求地址
     * @param params URL 查询参数（原始 Map）
     * @return 响应体字符串
     */
    public static String doOkHttpGet(String url, Map params) {
        Map<String, String> stringMap = new HashMap<>();
        params.forEach((key, value) -> stringMap.put(key.toString(), value.toString()));
        return doOkHttpGet(url, stringMap, null);
    }

    /**
     * 发送 GET 请求，将参数拼接为 URL 查询字符串。
     * <p>
     * 注意：参数键值直接拼接，不会进行URL编码。
     * </p>
     *
     * @param url     请求地址
     * @param params  URL 查询参数，可为 null
     * @param headers 自定义请求头，可为 null
     * @return 响应体字符串
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
     * 发送 GET 请求（JSONObject 参数，自动转为 Map）。
     * <p>
     * 将 {@link JSONObject} 中的键值对转为 Map 后，拼接到URL查询参数中。
     * </p>
     *
     * @param url     请求地址
     * @param params  URL 查询参数（JSONObject形式）
     * @param headers 自定义请求头，可为 null
     * @return 响应体字符串
     */
    public static String doOkHttpGet(String url, JSONObject params, Map<String, String> headers) {
        return doOkHttpGet(url, params.toJavaObject(Map.class), headers);
    }

}
