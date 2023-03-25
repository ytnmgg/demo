package com.hy.project.demo.auth.core.model;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
public enum SequenceNameEnum {
    /**
     * 用户id序列号
     */
    SEQ_USER_ID("10", "SEQ_USER_ID", "用户id序列号"),

    SEQ_ROLE_ID("11", "SEQ_ROLE_ID", "角色id序列号"),

    SEQ_PERMISSION_ID("12", "SEQ_PERMISSION_ID", "权限码id序列号"),

    SEQ_USER_ROLE_ID("13", "SEQ_USER_ROLE_ID", "用户角色关系id序列号"),

    SEQ_ROLE_PERMISSION_ID("14", "SEQ_ROLE_PERMISSION_ID", "角色权限码关系id序列号"),

    ;

    private String code;
    private String name;
    private String desc;

    SequenceNameEnum(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
