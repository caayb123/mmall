package com.mmall.service;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.List;
import java.util.Map;

public interface ShippingService {
    ServerResponse<Map<String,Object>> add(Integer userId, Shipping shipping);

    ServerResponse<String> delete(Integer userId,Integer shippingId);

    ServerResponse<String> update(Integer userId,Shipping shipping);

    ServerResponse<Shipping> selectOne(Integer userId,Integer shippingId);

    ServerResponse<PageInfo<Shipping>> selectAll(Integer userId, Integer pageNum, Integer pageSize);
}
