package com.hy.project.demo.auth.core.model;

import java.util.concurrent.atomic.AtomicLong;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
public class SequenceValue extends ToString {
    private static final long serialVersionUID = 9019245628736012973L;

    private long maxValue;
    private long minValue;
    private AtomicLong nextValue;
    private long step;
    private String name;

    public SequenceValue(String name, long step, long minValue, long maxValue) {
        this.name = name;
        this.step = step;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.nextValue = new AtomicLong(minValue);
    }

    public long getAndIncrement() {
        return nextValue.getAndIncrement();
    }

    public long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    public long getMinValue() {
        return minValue;
    }

    public void setMinValue(long minValue) {
        this.minValue = minValue;
    }

    public AtomicLong getNextValue() {
        return nextValue;
    }

    public void setNextValue(AtomicLong nextValue) {
        this.nextValue = nextValue;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
