package com.mmall.pojo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Getter
@Setter
public class PayInfo {
    @Id
    private Integer id;

    private Integer userId;

    private Long orderNo;

    private Integer payPlatform;

    private String platformNumber;

    private String platformStatus;

    private Date createTime;

    private Date updateTime;
}