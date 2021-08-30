package com.hy.project.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author rick.wl
 * @version : IndexController.java, v 0.1 2021年07月07日 10:49 上午 rick.wl Exp $
 */
@Controller
public class IndexController {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/**/{:[^.]*}")
    public String indexPage() {
        return "index";
    }
}