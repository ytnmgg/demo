package com.hy.project.demo.service.common;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.hy.project.demo.model.DemoResult;

/**
 * @author rick.wl
 * @date 2021/11/03
 */
public interface RedisService {

    /**
     * 设置值
     *
     * @param key key
     * @param value value
     * @return 结果
     */
    boolean set(final String key, Object value);

    /**
     * 设置值
     *
     * @param key key
     * @param value value
     * @param expireTime 超时时间
     * @param timeUnit 时间单位
     * @return 结果
     */
    boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit);

    /**
     * 是否存在key
     *
     * @param key key
     * @return 结果
     */
    boolean exists(final String key);

    /**
     * 获取值
     *
     * @param key key
     * @return 结果
     */
    Object get(final String key);

    /**
     * 删除key
     *
     * @param key key
     */
    void remove(final String key);

    /**
     * 重新设置超时时间
     *
     * @param key key
     * @param expireTime expireTime
     * @param timeUnit timeUnit
     */
    void expire(final String key, final Long expireTime, final TimeUnit timeUnit);

    /**
     * 查询以prefix开头的所有值
     *
     * @param prefix 开头串
     * @return 值列表
     */
    DemoResult<Map<String, Object>> list(String prefix);
}
