package com.mmall.common;

public enum CartCode {
    CHECKED(1,"购物车选中状态"),
    UN_CHECKED(0,"购物车未选中状态");
    private final int code;
    private final String value;

    CartCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
