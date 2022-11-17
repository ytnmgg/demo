package com.hy.project.demo.service.common.impl;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.hy.project.demo.exception.DemoException;
import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.service.common.RedisService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.hy.project.demo.constant.RedisConstants.KEY_LOCK;
import static com.hy.project.demo.constant.RedisConstants.LOCK_DURATION_SECONDS;
import static com.hy.project.demo.constant.RedisConstants.LOCK_TRY_MILLISECONDS;
import static com.hy.project.demo.constant.RedisConstants.REDIS_SCRIPT_RETURN_SUCCESS;
import static com.hy.project.demo.exception.DemoExceptionEnum.REDIS_LOCK_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/11/03
 */
@Service
public class RedisServiceImpl implements RedisService {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            LOGGER.error(String.format("failed to set key: %s cause of: %s", key, e.getMessage()), e);
        }
        return result;
    }

    @Override
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            LOGGER.error(
                String.format("failed to set key: %s with expire:%s cause of: %s", key, expireTime, e.getMessage()), e);
        }
        return result;
    }

    @Override
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    @Override
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    @Override
    public void expire(final String key, final Long expireTime, final TimeUnit timeUnit) {
        if (exists(key)) {
            redisTemplate.expire(key, expireTime, timeUnit);
        }
    }

    @Override
    public DemoResult<Map<String, Object>> list(String prefix) {
        Set<String> keySets = redisTemplate.keys(prefix + "*");

        if (CollectionUtils.isEmpty(keySets)) {
            return DemoResult.buildSuccessResult(null);
        }

        List<String> keys = new ArrayList<>(keySets);

        // Get multiple keys. Values are returned in the order of the requested keys.
        List<Object> values = redisTemplate.opsForValue().multiGet(keySets);

        Map<String, Object> kvs = keys.stream().collect(Collectors.toMap(k -> k, k -> values.get(keys.indexOf(k))));

        return DemoResult.buildSuccessResult(kvs);
    }

    @Override
    public void setHash(String hashName, Map hash) {
        redisTemplate.opsForHash().putAll(hashName, hash);
    }

    @Override
    public void setHash(String hashName, String key, Object value) {
        redisTemplate.opsForHash().put(hashName, key, value);
    }

    @Override
    public Object getHash(String hashName, String key) {
        return redisTemplate.opsForHash().get(hashName, key);
    }

    @Override
    public Map<String, Object> getHash(String hashName) {
        return redisTemplate.opsForHash().entries(hashName);
    }

    @Override
    public List<Object> multiGetHash(String hashName, Collection<String> keys) {
        return redisTemplate.opsForHash().multiGet(hashName, keys);
    }

    @Override
    public void removeHash(String hashName, final String... keys) {
        redisTemplate.opsForHash().delete(hashName, keys);
    }

    @Override
    public <R> R runWithLock(Supplier<R> supplier) {
        String uniqueValue = String.format("%s_%s", System.currentTimeMillis(), UUID.randomUUID());

        if (tryLock(uniqueValue)) {
            R result;
            try {
                result = supplier.get();
            } finally {
                if (unLock(KEY_LOCK, uniqueValue)) {
                    LOGGER.info("release lock success: [{}]", uniqueValue);
                } else {
                    LOGGER.error("release lock failed: [{}]", uniqueValue);
                }
            }

            return result;

        } else {
            throw new DemoException(REDIS_LOCK_EXCEPTION, "获取redis锁失败");
        }
    }

    @Override
    public Boolean addToZSet(String zSetName, String key, double score) {
        return redisTemplate.opsForZSet().add(zSetName, key, score);
    }

    @Override
    public Double incrementZSetScore(String zSetName, String key, double scoreDelta) {
        return redisTemplate.opsForZSet().incrementScore(zSetName, key, scoreDelta);
    }

    @Override
    public Long rankZSet(String zSetName, String key) {
        return redisTemplate.opsForZSet().rank(zSetName, key);
    }

    @Override
    public Set<String> reverseRangeZSet(String zSetName, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(zSetName, start, end);
    }

    @Override
    public Set<String> rangeZSetByScore(String zSetName, double min, double max, long offset,
        long count) {
        return redisTemplate.opsForZSet().rangeByScore(zSetName, min, max, offset, count);
    }

    @Override
    public Long sizeZSet(String zSetName) {
        return redisTemplate.opsForZSet().size(zSetName);
    }

    @Override
    public Long removeZSet(String zSetName, String... keys) {
        return redisTemplate.opsForZSet().remove(zSetName, keys);
    }

    private boolean tryLock(String uniqueValue) {
        long start = System.currentTimeMillis();
        for (; ; ) {
            LOGGER.info("try lock: [{}]", uniqueValue);
            Boolean getLock = redisTemplate.opsForValue().setIfAbsent(KEY_LOCK, uniqueValue,
                Duration.ofSeconds(LOCK_DURATION_SECONDS));
            if (BooleanUtils.isTrue(getLock)) {
                LOGGER.info("get lock success: [{}]", uniqueValue);
                return true;
            }
            long time = System.currentTimeMillis() - start;
            if (time > LOCK_TRY_MILLISECONDS) {
                LOGGER.info("get lock timeout: [{}]", uniqueValue);
                return false;
            }
        }
    }

    public boolean unLock(String key, String val) {
        String script
            = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object execute = redisTemplate.execute(redisScript, Collections.singletonList(key), val);
        return REDIS_SCRIPT_RETURN_SUCCESS.equals(execute);
    }

}
