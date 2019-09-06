package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderVo {
    private Long orderNo;
    private BigDecimal payment;
    private Integer paymentType;
    private String paymentTypeDesc;
    private Integer postage;
    private Integer status;
    private String statusDesc;
    private String paymentTime;
    private String sendTime;
    private String endTime;
    private String closeTime;
    private String createTime;
    private String imageHost;
    //单个商品订单明细
    private List<OrderItemVo> orderItemVoList;
    //具体的收获地址
    private String receiverName;
    private Integer shippingId;
    private ShippingVo shippingVo;
}
