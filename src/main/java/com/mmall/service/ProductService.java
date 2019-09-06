package com.mmall.service;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;



public interface ProductService {
    ServerResponse<PageInfo>  findAll(Integer pageNum, Integer pageSize);

    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> getDetails(Integer productId);

    ServerResponse<PageInfo> search(Integer pageNum,Integer pageSize,String productName,Integer productId);

    ServerResponse<ProductDetailVo> getDetailsToUser(Integer productId);

    ServerResponse<PageInfo> search(String keyword,Integer pageNum,Integer pageSize,Integer categoryId,String orderBy);
}
