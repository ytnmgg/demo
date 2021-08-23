/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.hy.project.demo.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.hy.project.demo.model.User;
import com.hy.project.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author rick.wl
 * @version : TestController.java, v 0.1 2021年07月07日 10:49 上午 rick.wl Exp $
 */
@RestController
public class TestController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    UserRepository userRepository;

    @CrossOrigin
    @GetMapping("/hello")
    public List<User> hello(String type, String name) {
        List<User> users  = userRepository.list(type, name);
        LOGGER.info("[LOGGER] users: {}", JSON.toJSONString(users));
        return users;
    }

    
}