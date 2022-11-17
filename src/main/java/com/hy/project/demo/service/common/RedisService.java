package com.hy.project.demo.service.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.hy.project.demo.model.DemoResult;

/**
 * @author rick.wl
 * @date 2021/11/03
 */
public interface RedisService {

    /**
     * 设置值
     *
     * @param key   key
     * @param value value
     * @return 结果
     */
    boolean set(final String key, Object value);

    /**
     * 设置值
     *
     * @param key        key
     * @param value      value
     * @param expireTime 超时时间
     * @param timeUnit   时间单位
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
     * @param key        key
     * @param expireTime expireTime
     * @param timeUnit   timeUnit
     */
    void expire(final String key, final Long expireTime, final TimeUnit timeUnit);

    /**
     * 查询以prefix开头的所有值
     *
     * @param prefix 开头串
     * @return 值列表
     */
    DemoResult<Map<String, Object>> list(String prefix);

    void setHash(final String hashName, final Map hash);

    void setHash(final String hashName, final String key, final Object value);

    Object getHash(final String hashName, final String key);

    Map<String, Object> getHash(final String hashName);

    List<Object> multiGetHash(String hashName, Collection<String> keys);

    void removeHash(final String hashName, final String... keys);

    <R> R runWithLock(Supplier<R> supplier);

    Boolean addToZSet(final String zSetName, final String key, final double score);

    Double incrementZSetScore(final String zSetName, final String key, final double scoreDelta);

    Long rankZSet(final String zSetName, final String key);

    Set<String> reverseRangeZSet(final String zSetName, final long start, final long end);

    Set<String> rangeZSetByScore(final String zSetName, final double min, final double max, final long offset,
        final long count);

    Long sizeZSet(final String zSetName);

    Long removeZSet(final String zSetName, final String... keys);
}
