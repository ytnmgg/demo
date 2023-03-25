package com.hy.project.demo.web.controller.monitor;

import javax.validation.Valid;

import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.monitor.facade.model.es.EsSearchRequest;
import com.hy.project.demo.monitor.facade.service.EsService;
import com.hy.project.demo.web.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/es")
public class EsController {

    @Autowired
    EsService esService;

    @GetMapping("/search.json")
    public AjaxResult search(@Valid EsSearchRequest request) throws Throwable {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        Object result = esService.search(request);
        return AjaxResult.success(result);
    }
}
