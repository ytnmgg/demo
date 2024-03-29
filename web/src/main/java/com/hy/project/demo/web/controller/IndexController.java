package com.hy.project.demo.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author rick.wl
 * @version : IndexController.java, v 0.1 2021年07月07日 10:49 上午 rick.wl Exp $
 */
@Controller
public class IndexController {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private Environment env;

    @RequestMapping(value = {"/", "/**"}, method = RequestMethod.GET)
    public String indexPage(Model model) {
        model.addAttribute("front_version", env.getProperty("front.version"));
        return "index";
    }

    public static void main(String[] args) {

    }
}