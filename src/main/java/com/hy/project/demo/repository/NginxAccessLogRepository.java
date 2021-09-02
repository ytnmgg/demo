package com.hy.project.demo.repository;

import java.util.Date;
import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.file.NginxAccessFileLine;
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
     * @param gmtBegin  开始日期
     * @param gmtEnd    结束日期
     * @param pageIndex 页码
     * @param pageSize  单页大小
     * @return 结果
     */
    PageResult<List<NginxAccessFileLine>> list(Date gmtBegin, Date gmtEnd, int pageIndex, int pageSize);

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
     * @param gmtEnd   结束
     * @return 结果
     */
    List<NginxAccessLogStatusCountDO> countStatus(Date gmtBegin, Date gmtEnd);
}
