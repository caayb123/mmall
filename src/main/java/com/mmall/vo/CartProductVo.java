package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class CartProductVo {
    //购物车内的某个产品的信息的抽象封装,一个购物车对象Cart可以对应一个购物车产品抽象对象
    private Integer id; //购物车id
    private Integer userId; //用户id
    private Integer productId; //产品id
    private Integer quantity;//购物车中此商品的数量
    private String productName; //产品名称
    private String productSubtitle; //产品副标题
    private String productMainImage; //产品主图
    private BigDecimal productPrice; //产品价格
    private Integer productStatus;  //产品状态
    private BigDecimal productTotalPrice; //该产品总价=该产品的价格*总数
    private Integer productStock;  //产品库存
    private Integer productChecked;//此商品是否勾选
    private String limitQuantity;//限制数量的一个返回结果
}
