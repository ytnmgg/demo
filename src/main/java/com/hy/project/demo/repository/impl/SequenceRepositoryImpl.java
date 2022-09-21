package com.hy.project.demo.repository.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hy.project.demo.exception.DemoException;
import com.hy.project.demo.model.sequence.SequenceValue;
import com.hy.project.demo.mybatis.entity.SequenceDO;
import com.hy.project.demo.mybatis.mapper.SequenceMapper;
import com.hy.project.demo.repository.SequenceRepository;
import com.hy.project.demo.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.hy.project.demo.exception.DemoExceptionEnum.ROW_LOCK_EXCEPTION;
import static com.hy.project.demo.exception.DemoExceptionEnum.UNEXPECTED;
import static com.hy.project.demo.util.CommonUtil.sleepQuietly;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
@Service
public class SequenceRepositoryImpl implements SequenceRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(SequenceRepositoryImpl.class);

    private final static long DEFAULT_STEP = 100;
    private final static long DEFAULT_MIN_VALUE = 1;
    private final static long MAX_RETRY_TIME = 10;
    private final static long DEFAULT_RETRY_WAIT_TIME_MS = 100;

    private final Map<String, SequenceValue> cache = new HashMap<>(20);
    private final Lock lock = new ReentrantLock();

    @Autowired
    SequenceMapper sequenceMapper;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long nextValue(String name) {
        SequenceValue cacheValue = this.cache.get(name);
        if (null == cacheValue) {
            return initCache(name);
        }

        return getNextValue(cacheValue);
    }

    /**
     * 初始化缓存，从db捞name对应的sequence记录
     *
     * @param name
     * @return
     */
    private long initCache(String name) {
        synchronized (this) {
            SequenceValue cacheValue = this.cache.get(name);
            if (null != cacheValue) {
                // 拿到锁后，发现已经被初始化好了，直接返回
                return getNextValue(cacheValue);

            } else {
                // 插入新行，需要处理主键冲突异常
                SequenceValue newValue;
                try {
                    newValue = insertToDb(name);

                } catch (DuplicateKeyException e) {
                    // 已存在
                    LOGGER.info("sequence already exist, name = {}", name);
                    newValue = makeNewValue(name);
                    updateFromDb(newValue);
                }

                cache.put(name, newValue);
                return getNextValue(newValue);
            }
        }
    }

    private long getNextValue(SequenceValue value) {
        long nextValue = value.getAndIncrement();
        if (nextValue > value.getMaxValue()) {
            lock.lock();
            try {
                updateFromDb(value);
            } finally {
                lock.unlock();
            }
            return getNextValue(value);
        } else {
            return nextValue;
        }
    }

    private SequenceValue insertToDb(String name) {
        LOGGER.info("insert new sequence to db, name = {}", name);
        SequenceDO sequenceDO = new SequenceDO();
        sequenceDO.setName(name);
        sequenceDO.setCurrentValue(DEFAULT_STEP);
        sequenceMapper.insert(sequenceDO);
        return makeNewValue(name);
    }

    private void updateFromDb(SequenceValue value) {
        int i = 0;
        for (; ; ) {
            SequenceDO sequenceDO = sequenceMapper.findByName(value.getName());
            AssertUtil.notNull(sequenceDO, UNEXPECTED, "未从db里面找到sequence：%s", value.getName());

            long dbValue = sequenceDO.getCurrentValue();
            long newMaxValue = dbValue + DEFAULT_STEP;
            long newMinValue = dbValue + 1;

            int cnt = sequenceMapper.compareAndSet(value.getName(), newMaxValue, dbValue);

            if (cnt < 1) {
                if (i++ < MAX_RETRY_TIME) {
                    LOGGER.info("插入sequence乐观锁异常，等待重试");
                    sleepQuietly(DEFAULT_RETRY_WAIT_TIME_MS);
                    continue;
                }
                throw new DemoException(ROW_LOCK_EXCEPTION, "插入sequence乐观锁异常");
            }

            value.setMinValue(newMinValue);
            value.setMaxValue(newMaxValue);
            value.getNextValue().set(newMinValue);

            LOGGER.info("sequence update: [{}]", value);
            return;
        }
    }

    private SequenceValue makeNewValue(String name) {
        return new SequenceValue(name, DEFAULT_STEP, DEFAULT_MIN_VALUE, DEFAULT_STEP);
    }
}