package com.hy.project.demo.repository;

import java.util.Date;
import java.util.List;

import com.hy.project.demo.model.file.NginxAccessFileLine;
import com.hy.project.demo.model.nginx.NginxAccessLogStatusCount;
import com.hy.project.demo.mybatis.entity.NginxAccessLogStatusCountDO;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public interface NginxAccessLogRepository {

    /**
     * 插入一行
     *
     * @param line 模型
     */
    void insert(NginxAccessFileLine line);

    /**
     * 列表查询
     *
     * @param gmtBegin 开始日期
     * @param gmtEnd   结束日期
     * @return 结果
     */
    List<NginxAccessFileLine> list(Date gmtBegin, Date gmtEnd);

    /**
     * 查询最近的一条
     *
     * @return 结果
     */
    NginxAccessFileLine getLatest();

    /**
     * 统计状态
     *
     * @param gmtBegin 开始
     * @param gmtEnd 结束
     * @return 结果
     */
    List<NginxAccessLogStatusCountDO> countStatus(Date gmtBegin, Date gmtEnd);
}
