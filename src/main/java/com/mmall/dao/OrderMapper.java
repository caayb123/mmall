package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Repository
public interface OrderMapper extends Mapper<Order> {

   Order selectByUserIdOrderNo(@Param(value = "userId")Integer userId,@Param(value = "orderNo") Long orderNo);

   Order selectByOrderNo(@Param(value = "orderNo")Long orderNo);

   int updateStatus(Order order);

   int insertOne(Order order);

  int updateStatusById(@Param(value = "status")Integer status,@Param(value = "orderNo")Long orderNo);

  List<Order> selectByUserId(@Param(value = "userId") Integer userId);

  List<Order> findAll();

  int updateStatusSendTime(Order order);
}