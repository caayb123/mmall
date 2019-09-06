package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ProductCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.CategoryService;
import com.mmall.service.ProductService;
import com.mmall.utils.DateUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("productService")
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryService categoryService;
    @Override
    public ServerResponse<PageInfo> findAll(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectAll();
        List<ProductListVo> productListVoList=new ArrayList<>();
        for (Product product:products) {
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo=new PageInfo<>(products);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {
        if (StringUtils.isNotBlank(product.getSubImages())){
            String[] subImageArrary = product.getSubImages().split(",");
            //第一张图片作为首图
            product.setMainImage(subImageArrary[0]);
        }
        if (product.getId()!=null){
            //更新
            int result = productMapper.updateByPrimaryKey(product);
            if (result==1) {
                product.setUpdateTime(new Date());
                return ServerResponse.createBySuccessMessage("产品更新成功");
            }
        }else {
            //增加
            product.setCreateTime(new Date());
            product.setUpdateTime(new Date());
            int result = productMapper.insert(product);
            if (result==1) {
                return ServerResponse.createBySuccessMessage("产品新增成功");
            }
        }
     return ServerResponse.createByErrorMessage("产品新增或更新失败");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
         Product product=new Product();
         product.setStatus(status);
         product.setId(productId);
         product.setUpdateTime(new Date());
        int result = productMapper.updateByPrimaryKeySelective(product);
        if (result==1){
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> getDetails(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("产品不存在或已下架");
        }
        return ServerResponse.createBySuccess(assembleProductDetailVo(product));
    }

    @Override
    public ServerResponse<PageInfo> search(Integer pageNum, Integer pageSize, String productName, Integer productId) {
        if (StringUtils.isNotBlank(productName)){
            productName="%"+productName+"%";
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.searchLike(productName, productId);
        List<ProductListVo> productListVoList=new ArrayList<>();
        for (Product product:products) {
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo=new PageInfo<>(products);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVo>getDetailsToUser(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null||product.getStatus()== ProductCode.OFF_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品不存在或已下架");
        }
        return ServerResponse.createBySuccess(assembleProductDetailVo(product));
    }

    @Override
    public ServerResponse<PageInfo> search(String keyword, Integer pageNum, Integer pageSize, Integer categoryId,String orderBy) {
        if (StringUtils.isBlank(keyword)&&categoryId==null){
            return ServerResponse.createByErrorMessage("参数有误");
        }
        Set<Integer> data =new HashSet<>();
        if (categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&StringUtils.isBlank(keyword)){
                //没有该分类同时没有该关键字，返回空结果集不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> list=new ArrayList<>();
                PageInfo pageInfo=new PageInfo(list);
               return ServerResponse.createBySuccess(pageInfo);
            }
            data = categoryService.getChildDeepCategory(categoryId).getData();
        }
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(keyword)){
            keyword="%"+keyword+"%";
        }
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
             if (Const.PRICE_ASC_DESC.contains(orderBy)){
                  PageHelper.orderBy(orderBy);
             }
        }
        List<Product> list = productMapper.findByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, data.size() == 0 ? null : data);
        List<ProductListVo> listVos=new ArrayList<>();
        for (Product product: list
             ) {
           listVos.add(assembleProductListVo(product));
        }
        PageInfo pageInfo=new PageInfo(list);
        pageInfo.setList(listVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        try {
            BeanUtils.copyProperties(productDetailVo,product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("系统异常");
        }
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "https://cybimage.oss-cn-shenzhen.aliyuncs.com/"));
          //查找是否有父节点
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category==null){
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }
   private ProductListVo assembleProductListVo(Product product){
       ProductListVo productListVo=new ProductListVo();
       try {
           BeanUtils.copyProperties(productListVo,product);
       } catch (Exception e) {
           e.printStackTrace();
          throw new RuntimeException("系统异常");
       }
       productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "https://cybimage.oss-cn-shenzhen.aliyuncs.com/"));
       return productListVo;
   }

}
