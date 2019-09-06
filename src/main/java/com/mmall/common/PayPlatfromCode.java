package com.mmall.common;

public enum PayPlatfromCode {
    ALIPAY(1,"支付宝"),
    ONLINE_PAY(11,"在线支付");
    PayPlatfromCode(int code,String value){
        this.code = code;
        this.value = value;
    }
    private String value;
    private int code;

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static String getDescByCode(int code){
        for (PayPlatfromCode value : values()) {
            if (code==value.getCode()){
                return value.getValue();
            }
        }
       throw new RuntimeException("没有找到对应的枚举");
    }
}
