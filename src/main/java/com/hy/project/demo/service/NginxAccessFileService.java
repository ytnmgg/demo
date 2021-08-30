package com.hy.project.demo.service;

import java.util.Date;
import java.util.List;

import com.hy.project.demo.model.file.NginxAccessFileLine;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public interface NginxAccessFileService {

    /**
     * 查询日志列表
     *
     * @param gmtBegin 开始日期
     * @param gmtEnd 结束日期
     * @return 结果
     */
    List<NginxAccessFileLine> listLines(Date gmtBegin, Date gmtEnd);

    /**
     * 读取并存储
     */
    void readAndStoreLines();
}
