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
public class Product {
    @Id
    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private String subImages;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}