package com.hy.project.demo.web.model;

import javax.validation.constraints.NotBlank;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2021/12/02
 */
public class RedisDeleteRequest extends ToString {

    private static final long serialVersionUID = -821694433271510407L;

    @NotBlank(message = "删除的key不能为空")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
