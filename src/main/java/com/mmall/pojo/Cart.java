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
public class Cart {
    @Id
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;
}