package com.hy.project.demo.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rick.wl
 * @date 2022/09/21
 */
@RestController
public class DruidStatController {
    @GetMapping("/druid/stat.json")
    public Object druidStat(){
        // DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据，除此之外 DruidStatManagerFacade 还提供了一些其他方法，你可以按需选择使用。
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
