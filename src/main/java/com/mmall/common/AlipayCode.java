package com.mmall.common;

public enum AlipayCode {

    CANCELED(0,"已取消"),
    NO_PAY(10,"未支付"),
    PAID(20,"已付款"),
    SHIPPED(40,"已发货"),
    ORDER_SUCCESS(50,"订单完成"),
    ORDER_CLOSE(60,"订单关闭");

    private final int code;
    private final String value;

    AlipayCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
    public static String getDescByCode(int code){
        for (AlipayCode value : values()) {
            if (code==value.getCode()){
                return value.getValue();
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }
}
