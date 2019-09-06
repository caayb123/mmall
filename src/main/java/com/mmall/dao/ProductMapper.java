package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Repository
public interface ProductMapper extends Mapper<Product> {
     List<Product> searchLike(@Param(value = "productName")String productName,@Param(value = "productId")Integer productId);
     List<Product> findByNameAndCategoryIds(@Param(value = "productName")String productName,@Param(value = "categoryIds") Set<Integer> categoryIds);
}