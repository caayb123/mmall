package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Repository
public interface CartMapper extends Mapper<Cart> {
      int deleteByUserIdProductIds(@Param(value = "userId") Integer userId, @Param(value = "productIdList") List<String> productIdList);
      int updateChecked(@Param(value = "checked")Integer checked,@Param(value = "userId")Integer userId,@Param(value = "productId") Integer productId);
      int selectCartProductCount(@Param(value = "userId") Integer userId);
}