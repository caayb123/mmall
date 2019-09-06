package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Repository
public interface ShippingMapper extends Mapper<Shipping> {
    int add(Shipping shipping);
    int update(Shipping shipping);
}