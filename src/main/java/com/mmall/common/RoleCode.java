package com.mmall.common;

public enum RoleCode {
    ROLE_USER(0,"ROLE_USER"),
    ROLE_ADMIN(1,"ROLE_ADMIN");

    private final int code;
    private final String desc;

    RoleCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
