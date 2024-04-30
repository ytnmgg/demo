package com.hy.project.app.example.service.impl;

import com.hy.project.app.example.service.SomeService;
import com.hy.project.app.sdk.service.ContainerServiceOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SomeServiceImpl implements SomeService {

    @Autowired
    ContainerServiceOne containerServiceOne;


    @Override
    public String sayHello(String name) {
        return "Hello From: " + containerServiceOne.sayHello(name);
    }
}
