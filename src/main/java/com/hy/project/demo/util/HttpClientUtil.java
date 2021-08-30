package com.hy.project.demo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import com.hy.project.demo.exception.DemoException;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static com.hy.project.demo.exception.DemoExceptionEnum.HTTP_EXCEPTION;
import static com.hy.project.demo.exception.DemoExceptionEnum.REMOTE_BIZ_EXCEPTION;
import static com.hy.project.demo.exception.DemoExceptionEnum.REMOTE_UNKNOWN_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public class HttpClientUtil {
    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * http client
     */
    private static CloseableHttpClient httpClient;

    /**
     * 获取客户端（单例模式）
     *
     * @return 结果
     */
    public static CloseableHttpClient getClient() {
        if (null != httpClient) {
            return httpClient;
        }

        synchronized (HttpClientUtil.class) {
            if (null == httpClient) {
                httpClient = buildClient();
            }
        }

        return httpClient;
    }

    /**
     * 构造http客户端
     *
     * @return 结果
     */
    private static CloseableHttpClient buildClient() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000)
            .setConnectTimeout(3000).setConnectionRequestTimeout(2000).build();

        return HttpClients.custom().setMaxConnTotal(32).setMaxConnPerRoute(32)
            .setDefaultRequestConfig(requestConfig).evictExpiredConnections()
            .evictIdleConnections(60L, TimeUnit.SECONDS).build();
    }

    /**
     * 构建http请求（POST方式、JSON内容）
     *
     * @param url     请求地址 (相对路径)
     * @param json    请求JSON字符串
     * @param headers 头信息
     * @return 结果
     */
    public static String postJson(String url, String json, Header[] headers) {

        // URL
        final HttpPost httpPost = new HttpPost(url);

        // 头信息
        httpPost.setHeaders(headers);

        // JSON body
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        // 执行调用
        return httpExecute(getClient(), httpPost);
    }

    /**
     * 构建http请求（POST方式、JSON内容）
     *
     * @param url     请求地址 (相对路径)
     * @param json    请求JSON字符串
     * @param headers 头信息
     * @return 结果
     */
    public static String putJson(String url, String json, Header[] headers) {

        // URL
        final HttpPut httpPut = new HttpPut(url);

        // 头信息
        httpPut.setHeaders(headers);

        // JSON body
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPut.setEntity(entity);

        // 执行调用
        return httpExecute(getClient(), httpPut);
    }

    /**
     * 构建http请求（GET方式）
     *
     * @param url     请求地址 (相对路径)
     * @param params  参数
     * @param headers 头信息
     * @return 结果
     */
    public static String getJson(String url, Map<String, String> params, Header[] headers) {
        // URL
        StringBuilder finalUrl = new StringBuilder(url);
        if (!CollectionUtils.isEmpty(params)) {
            finalUrl.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                finalUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        final HttpGet httpGet = new HttpGet(finalUrl.toString());

        // 头信息
        httpGet.setHeaders(headers);

        // 执行调用
        return httpExecute(getClient(), httpGet);
    }

    /**
     * 执行http调用
     *
     * @param httpClient httpClient
     * @param req        http请求,可以是get也可以是post
     * @return 返回值(只有http返回码为200时才会返回, 不然会抛出异常)
     */
    private static String httpExecute(CloseableHttpClient httpClient, HttpUriRequest req) {
        CloseableHttpResponse response = null;
        try {
            LOGGER.info("执行http调用：req={}", JSON.toJSONString(req));

            response = httpClient.execute(req);

            AssertUtil.notNull(response, REMOTE_UNKNOWN_EXCEPTION, "http调用返回null，request=%s",
                JSON.toJSONString(req));

            String res = EntityUtils.toString(response.getEntity());

            LOGGER.info("http调用返回：response={}，res={}", JSON.toJSONString(req), res);

            AssertUtil.notNull(response.getStatusLine(), REMOTE_BIZ_EXCEPTION, "http调用返回空statusLine");
            AssertUtil.equals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK,
                REMOTE_BIZ_EXCEPTION, "http调用返回非200结果码：code=%s, res=%s",
                response.getStatusLine().getStatusCode(), res);

            return res;

        } catch (Exception e) {
            LOGGER.error(String.format("http调用失败：%s", e.getMessage()), e);
            throw new DemoException(HTTP_EXCEPTION, e, "http调用失败");

        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                LOGGER.error(String.format("关闭http连接失败：%s", e.getMessage()), e);
            }
        }
    }

    public static Header[] buildCommonHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));

        return headers.toArray(new Header[0]);
    }
}
