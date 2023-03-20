package com.hy.project.demo.service.es;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;

/**
 * @author rick.wl
 * @date 2023/03/15
 */
public interface AstService {
    /**
     * 从sql语句构建es query
     * @param sql
     * @return
     */
    Query buildEsQuery(String sql);
}
