package com.mmall.pojo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Getter
@Setter
public class Order {
    @Id
    private Integer id;

    private Long orderNo;

    private Integer userId;

    private Integer shippingId;

    private BigDecimal payment;

    private Integer paymentType;

    private Integer postage;

    private Integer status;

    private Date paymentTime;

    private Date sendTime;

    private Date endTime;

    private Date closeTime;

    private Date createTime;

    private Date updateTime;
}