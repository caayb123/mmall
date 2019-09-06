package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class CartVo {
    //总的购物车
    private List<CartProductVo> cartProductVoList;   //购物车内所有商品信息
    private BigDecimal cartTotalPrice;    //购物车内所有商品价格
    private Boolean allChecked;//是否已经都勾选
    private String imageHost; //商品图片主地址
}
