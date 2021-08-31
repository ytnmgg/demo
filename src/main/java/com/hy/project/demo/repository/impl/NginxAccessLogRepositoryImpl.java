package com.hy.project.demo.repository.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hy.project.demo.model.file.NginxAccessFileLine;
import com.hy.project.demo.model.nginx.NginxAccessLogStatusCount;
import com.hy.project.demo.mybatis.entity.NginxAccessLogDO;
import com.hy.project.demo.mybatis.entity.NginxAccessLogStatusCountDO;
import com.hy.project.demo.mybatis.mapper.NginxAccessLogMapper;
import com.hy.project.demo.repository.NginxAccessLogRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@Repository
public class NginxAccessLogRepositoryImpl implements NginxAccessLogRepository {

    @Autowired
    NginxAccessLogMapper nginxAccessLogMapper;

    @Override
    public void insert(NginxAccessFileLine line) {
        nginxAccessLogMapper.insert(modelToDo(line));
    }

    @Override
    public List<NginxAccessFileLine> list(Date gmtBegin, Date gmtEnd) {
        List<NginxAccessLogDO> logDOs = nginxAccessLogMapper.list(gmtBegin, gmtEnd);
        if (CollectionUtils.isEmpty(logDOs)) {
            return null;
        }
        return logDOs.stream().map(this::doToModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public NginxAccessFileLine getLatest() {
        NginxAccessLogDO logDO = nginxAccessLogMapper.getLatest();
        if (null == logDO) {
            return null;
        }

        return doToModel(logDO);
    }

    @Override
    public NginxAccessLogStatusCount countStatus() {
        List<NginxAccessLogStatusCountDO> dos = nginxAccessLogMapper.countStatus();

        NginxAccessLogStatusCount count = new NginxAccessLogStatusCount();
        if (CollectionUtils.isEmpty(dos)) {
            return count;
        }

        long countOf200 = 0;
        long countOf3xx = 0;
        long countOf4xx = 0;
        long countOf5xx = 0;
        long countOfOthers = 0;
        for(NginxAccessLogStatusCountDO countDo : dos) {
            count.getStatusCount().put(countDo.getStatus(), countDo.getCount());

            if ("200".equals(countDo.getStatus())) {
                countOf200 = countDo.getCount();
            } else if (countDo.getStatus().startsWith("3")) {
                countOf3xx += countDo.getCount();
            } else if (countDo.getStatus().startsWith("4")) {
                countOf4xx += countDo.getCount();
            } else if (countDo.getStatus().startsWith("5")) {
                countOf5xx += countDo.getCount();
            } else {
                countOfOthers += countDo.getCount();
            }
        }

        count.setCountOf200(countOf200);
        count.setCountOf3xx(countOf3xx);
        count.setCountOf4xx(countOf4xx);
        count.setCountOf5xx(countOf5xx);
        count.setCountOfOthers(countOfOthers);
        return count;
    }

    private NginxAccessLogDO modelToDo(NginxAccessFileLine model) {
        if (null == model) {
            return null;
        }
        NginxAccessLogDO logDO = new NginxAccessLogDO();
        logDO.setRemoteAddress(model.getRemoteAddress());
        logDO.setTimeLocal(model.getTimeLocal());
        logDO.setRequest(model.getRequest());
        logDO.setStatus(model.getStatus());
        logDO.setBodyBytes(model.getBodyBytes());
        logDO.setHttpReferer(model.getHttpReferer());
        logDO.setHttpUserAgent(model.getHttpUserAgent());
        logDO.setFileMarker(model.getFileMarker());
        logDO.setLineNumber(model.getLineNumber());
        logDO.setLineContent(model.getLineContent());
        return logDO;
    }

    private NginxAccessFileLine doToModel(NginxAccessLogDO logDO) {
        if (null == logDO) {
            return null;
        }
        NginxAccessFileLine model = new NginxAccessFileLine();
        model.setGmtCreate(logDO.getGmtCreate());
        model.setGmtModified(logDO.getGmtModified());
        model.setId(logDO.getId());
        model.setRemoteAddress(logDO.getRemoteAddress());
        model.setTimeLocal(logDO.getTimeLocal());
        model.setRequest(logDO.getRequest());
        model.setStatus(logDO.getStatus());
        model.setBodyBytes(logDO.getBodyBytes());
        model.setHttpReferer(logDO.getHttpReferer());
        model.setHttpUserAgent(logDO.getHttpUserAgent());
        model.setFileMarker(logDO.getFileMarker());
        model.setLineContent(logDO.getLineContent());
        model.setLineNumber(logDO.getLineNumber());
        return model;
    }
}
