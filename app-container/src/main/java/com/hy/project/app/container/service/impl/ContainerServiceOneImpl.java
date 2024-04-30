package com.hy.project.app.container.service.impl;

import com.hy.project.app.sdk.service.ContainerServiceOne;
import com.hy.project.app.container.service.ContainerServiceTwo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContainerServiceOneImpl implements ContainerServiceOne {
    @Autowired
    ContainerServiceTwo containerServiceTwo;

    @Override
    public String sayHello(String name) {
        return containerServiceTwo.sayHello(name);
    }
}
