package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.ShippingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("shippingService")
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Map<String,Object>> add(Integer userId, Shipping shipping) {
        if (StringUtils.isBlank(shipping.getReceiverName())||StringUtils.isBlank(shipping.getReceiverAddress())){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        if (StringUtils.isBlank(shipping.getReceiverPhone())&&StringUtils.isBlank(shipping.getReceiverMobile())){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        shipping.setUserId(userId);
        int result = shippingMapper.add(shipping);
        if (result>0){
            Map<String,Object> map=new HashMap<>();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",map);
        }
       return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse<String> delete(Integer userId,Integer shippingId) {
        if (shippingId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Shipping shipping=new Shipping();
        shipping.setId(shippingId);
        shipping.setUserId(userId);
        int result = shippingMapper.delete(shipping);
        if (result>0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse<String> update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int result = shippingMapper.update(shipping);
        if (result>0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> selectOne(Integer userId, Integer shippingId) {
        if (shippingId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Shipping shipping=new Shipping();
        shipping.setUserId(userId);
        shipping.setId(shippingId);
        shipping = shippingMapper.selectOne(shipping);
        if (shipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse<PageInfo<Shipping>> selectAll(Integer userId,Integer pageNum,Integer pageSize) {
        Shipping shipping=new Shipping();
        shipping.setUserId(userId);
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> resultList = shippingMapper.select(shipping);
        PageInfo<Shipping> pageInfo=new PageInfo<>(resultList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
