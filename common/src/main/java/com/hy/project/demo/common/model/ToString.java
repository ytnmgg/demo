package com.hy.project.demo.common.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public class ToString implements Serializable {

    private static final long serialVersionUID = -4010008247422297976L;

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
