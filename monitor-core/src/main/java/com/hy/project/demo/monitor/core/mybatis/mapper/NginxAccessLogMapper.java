package com.hy.project.demo.monitor.core.mybatis.mapper;

import java.util.Date;
import java.util.List;

import com.hy.project.demo.monitor.core.mybatis.entity.NginxAccessLogDO;
import com.hy.project.demo.monitor.core.mybatis.entity.NginxAccessLogPointDO;
import com.hy.project.demo.monitor.core.mybatis.entity.NginxAccessLogStatusCountDO;
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
     * @param gmtEnd   结束时间
     * @param offset   偏移量
     * @param pageSize 单页大小
     * @return 结果
     */
    List<NginxAccessLogDO> list(@Param("gmtBegin") Date gmtBegin, @Param("gmtEnd") Date gmtEnd,
        @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 分页计数
     *
     * @param gmtBegin 开始时间
     * @param gmtEnd   结束时间
     * @return 结果
     */
    long countForList(@Param("gmtBegin") Date gmtBegin, @Param("gmtEnd") Date gmtEnd);

    /**
     * 查找最近的一条
     *
     * @return 结果
     */
    NginxAccessLogDO getLatest();

    /**
     * 统计状态
     *
     * @param gmtBegin 开始时间
     * @param gmtEnd   结束时间
     * @return 结果
     */
    List<NginxAccessLogStatusCountDO> countStatus(@Param("gmtBegin") Date gmtBegin, @Param("gmtEnd") Date gmtEnd);

    /**
     * 拉取按小时统计的状态个数列表
     *
     * @param gmtBegin 开始日期
     * @param gmtEnd 结束日期
     * @param status 状态
     * @return 结果
     */
    List<NginxAccessLogPointDO> listPoints(@Param("gmtBegin") Date gmtBegin, @Param("gmtEnd") Date gmtEnd,
        @Param("status") String status);
}
