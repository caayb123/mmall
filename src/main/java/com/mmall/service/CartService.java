package com.mmall.service;

import com.mmall.common.ServerResponse;

import com.mmall.vo.CartVo;

public interface CartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> delete(Integer userId,String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> updateAllCheckOrUnCheck(Integer checked,Integer userId);

    ServerResponse<CartVo> updateOneCheckOrUnCheck(Integer checked,Integer userId,Integer productId);

    ServerResponse<Integer>  getCartProductCount(Integer userId);
}
