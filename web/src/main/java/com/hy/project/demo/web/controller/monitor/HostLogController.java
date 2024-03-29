package com.hy.project.demo.web.controller.monitor;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.common.util.DateUtil;
import com.hy.project.demo.monitor.facade.model.file.NginxAccessFileLine;
import com.hy.project.demo.monitor.facade.model.nginx.NginxAccessLogPointModel;
import com.hy.project.demo.monitor.facade.model.nginx.NginxAccessLogStatusCount;
import com.hy.project.demo.monitor.facade.service.NginxAccessFileService;
import com.hy.project.demo.web.model.NginxLogListPointsRequest;
import com.hy.project.demo.web.model.NginxLogListRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
@RestController
@RequestMapping("/host/log")
public class HostLogController {
    //
    //private final static Logger LOGGER = LoggerFactory.getLogger(HostLogController.class);
    //
    //@Autowired
    //NginxAccessFileService nginxAccessFileService;
    //
    //@GetMapping("/nginx/list.json")
    //public PageResult<List<NginxAccessFileLine>> listNginxLogs(@Valid NginxLogListRequest request) {
    //
    //    AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
    //    Date gmtBegin = null;
    //    Date gmtEnd = null;
    //
    //    if (StringUtils.isNotBlank(request.getGmtBegin())) {
    //        gmtBegin = DateUtil.parse(request.getGmtBegin(), DateUtil.STANDARD_STR);
    //    }
    //
    //    if (StringUtils.isNotBlank(request.getGmtEnd())) {
    //        gmtEnd = DateUtil.parse(request.getGmtEnd(), DateUtil.STANDARD_STR);
    //    }
    //
    //    return nginxAccessFileService.listLines(gmtBegin, gmtEnd, request.getPageIndex(), request.getPageSize());
    //}
    //
    //@GetMapping("/nginx/status.json")
    //public NginxAccessLogStatusCount countNginxStatus() {
    //    return nginxAccessFileService.countStatus();
    //}
    //
    //@GetMapping("/nginx/listPoints.json")
    //public List<NginxAccessLogPointModel> listNginxPoints(@Valid NginxLogListPointsRequest request) {
    //    Date begin = DateUtil.parse(request.getGmtBegin(), DateUtil.STANDARD_STR);
    //    Date end = DateUtil.parse(request.getGmtEnd(), DateUtil.STANDARD_STR);
    //    return nginxAccessFileService.listPoints(begin, end);
    //}

}