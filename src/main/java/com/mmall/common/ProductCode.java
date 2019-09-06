package com.mmall.common;

public enum ProductCode {
     ON_SALE(1,"在线"),
     OFF_SALE(0,"下线");

  private final int code;
  private final String value;

     ProductCode(int code, String value) {
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
