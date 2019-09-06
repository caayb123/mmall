package com.mmall.controller;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.ProductService;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @RequestMapping(value = "/detail.do",method = RequestMethod.GET)
    @ResponseBody
   public ServerResponse<ProductDetailVo> detail(Integer productId){
        if (productId==null){
           return ServerResponse.createByErrorMessage("参数有误");
        }
       return productService.getDetailsToUser(productId);
   }
    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(String keyword, Integer categoryId, @RequestParam(defaultValue = "1")Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize,String orderBy){
       return productService.search(keyword,pageNum,pageSize,categoryId,orderBy);
    }
}
