package com.hy.project.app.container.service.impl;


import com.hy.project.app.container.service.ContainerServiceTwo;
import org.springframework.stereotype.Service;

@Service
public class ContainerServiceTwoImpl implements ContainerServiceTwo {
    @Override
    public String sayHello(String name) {
        return "Hello From Service Two: " + name;
    }
}
