package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Repository
public interface OrderItemMapper extends Mapper<OrderItem> {
      void batchInsert(@Param(value = "list") List<OrderItem> list);
}