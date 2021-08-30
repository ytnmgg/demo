package com.hy.project.demo.mybatis.mapper;

import java.util.Date;
import java.util.List;

import com.hy.project.demo.mybatis.entity.NginxAccessLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@Mapper
public interface NginxAccessLogMapper {

    /**
     * 插入
     *
     * @param logDo DO
     */
    void insert(NginxAccessLogDO logDo);

    /**
     * 获取全部用户
     *
     * @param gmtBegin 开始时间
     * @param gmtEnd 结束时间
     * @return 结果
     */
    List<NginxAccessLogDO> list(@Param("gmtBegin") Date gmtBegin, @Param("gmtEnd") Date gmtEnd);

    /**
     * 查找最近的一条
     *
     * @return 结果
     */
    NginxAccessLogDO getLatest();
}
