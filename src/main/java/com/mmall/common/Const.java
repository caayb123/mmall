package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
      * @Description: 常量
      * @Author: cyb
      */
public class Const {
    public static final String CURRENT_USER="CURRENT_USER";  //当前用户
     public static final String EMAIL="email"; //邮箱
     public static final String USERNAME="username"; //用户名
     public static final String SALT="salt"; //MD5盐值
     public static final  Set<String> PRICE_ASC_DESC=Sets.newHashSet("price desc","price asc"); //排序校验
    public static final String LIMT_NUM_FAIL="LIMT_NUM_FAIL";    //购物车单个产品库存限制
    public static final String LIMT_NUM_SUCCESS="LIMT_NUM_SUCCESS";//购物车单个产品库存限制
   public static String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";  //支付宝等待支付状态
    public static  String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS"; //支付宝支付完成
    public static  String RESPONSE_SUCCESS = "success";   //提供给支付服务器的参数，一定要是success否则支付宝会一直回调
    public static String RESPONSE_FAILED = "failed";

}
