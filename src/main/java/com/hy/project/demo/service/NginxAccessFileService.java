package com.hy.project.demo.service;

import java.util.Date;
import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.file.NginxAccessFileLine;
import com.hy.project.demo.model.nginx.NginxAccessLogPointModel;
import com.hy.project.demo.model.nginx.NginxAccessLogStatusCount;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public interface NginxAccessFileService {

    /**
     * 查询日志列表
     *
     * @param gmtBegin  开始日期
     * @param gmtEnd    结束日期
     * @param pageIndex index
     * @param pageSize  页码
     * @return 结果
     */
    PageResult<List<NginxAccessFileLine>> listLines(Date gmtBegin, Date gmtEnd, int pageIndex, int pageSize);

    /**
     * 读取并存储
     */
    void readAndStoreLines();

    /**
     * 统计状态
     *
     * @return 结果
     */
    NginxAccessLogStatusCount countStatus();

    /**
     * 拉取按小时统计的状态个数列表
     *
     * @param gmtBegin 开始日期
     * @param gmtEnd 结束日期
     * @return 结果
     */
    List<NginxAccessLogPointModel> listPoints(Date gmtBegin, Date gmtEnd);
}
