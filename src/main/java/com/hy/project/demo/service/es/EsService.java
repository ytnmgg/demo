package com.hy.project.demo.service.es;

import com.hy.project.demo.model.es.EsSearchRequest;

/**
 * @author rick.wl
 * @date 2023/02/17
 */
public interface EsService {
    Object search(EsSearchRequest request) throws Throwable;
}
