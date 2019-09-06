package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface OrderService {

   ServerResponse<Map<String,String>> pay(Integer userId,Long orderNo,String path);

   ServerResponse checkPay(Map<String,String> params);

   ServerResponse queryStatus(Integer userId,Long orderNo);

   ServerResponse createOrder(Integer userId,Integer shippingId);

   ServerResponse  cancelOrder(Integer userId,Long orderNo);

   ServerResponse getOrderCartProduct(Integer userId);

   ServerResponse<OrderVo> detail(Integer userId, Long orderNo);

   ServerResponse<PageInfo> getOrderList(Integer userId,Integer pageNum,Integer pageSize);

   ServerResponse<PageInfo> findAll(Integer pageNum,Integer pageSize);

   ServerResponse<OrderVo> searchByOrderNo(Long orderNo);

   ServerResponse updateSendStatus(Long orderNo);
}
