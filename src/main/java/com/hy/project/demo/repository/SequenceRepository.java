package com.hy.project.demo.repository;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
public interface SequenceRepository {

    long nextValue(String name);
}
