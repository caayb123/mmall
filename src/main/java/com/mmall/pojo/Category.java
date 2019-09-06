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
public class Category {
    @Id
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;
}