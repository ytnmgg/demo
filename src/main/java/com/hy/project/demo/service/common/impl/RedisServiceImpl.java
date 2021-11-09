package com.hy.project.demo.service.common.impl;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.hy.project.demo.service.common.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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

}
