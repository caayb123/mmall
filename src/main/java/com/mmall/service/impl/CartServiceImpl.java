package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.mmall.common.CartCode;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.CartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service("cartService")
public class CartServiceImpl implements CartService {
     @Autowired
     private CartMapper cartMapper;
     @Autowired
     private ProductMapper productMapper;
    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId==null||count==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Cart cartResult = getOneCart(userId, productId);
        if (cartResult==null){
             //购物车无该商品直接新增
             Cart cart=new Cart();
             cart.setProductId(productId);
             cart.setUserId(userId);
             cart.setQuantity(count);
             cart.setProductId(productId);
             cart.setUserId(userId);
             cart.setChecked(CartCode.CHECKED.getCode());
             cart.setCreateTime(new Date());
             cart.setUpdateTime(new Date());
              cartMapper.insert(cart);
         }else {
             //购物车有该商品,数目增加
             count=cartResult.getQuantity()+count;
             cartResult.setQuantity(count);
             cartResult.setUpdateTime(new Date());
             cartMapper.updateByPrimaryKeySelective(cartResult);
         }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId==null||count==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Cart cartResult = getOneCart(userId, productId);
        if (cartResult!=null){
            cartResult.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cartResult);
        }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> delete(Integer userId, String productIds) {
        if (StringUtils.isBlank(productIds)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        String[] split = productIds.split(",");
        List<String> productList= Arrays.asList(split);
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        return ServerResponse.createBySuccess(getCartVoLimt(userId));
    }

    @Override
    public ServerResponse<CartVo> updateAllCheckOrUnCheck(Integer checked, Integer userId) {
        if (checked==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        cartMapper.updateChecked(checked,userId,null);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> updateOneCheckOrUnCheck(Integer checked, Integer userId, Integer productId) {
        if (checked==null||productId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        cartMapper.updateChecked(checked, userId, productId);
        return list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    //高复用性核心方法计算购物车内商品的价格，信息等,  相当于获取用户的总购物车
    private CartVo getCartVoLimt(Integer userId){
        CartVo cartVo=new CartVo();
        //先查出该用户购物车内所有的商品信息
        Cart cart=new Cart();
        cart.setUserId(userId);
        List<Cart> userCart = cartMapper.select(cart);
        List<CartProductVo> cartProductVoList=new ArrayList<>(); //初始化总体购物车产品抽象对象
        BigDecimal cartTotalPrice = new BigDecimal("0");  //购物车总价初始化
        //判断该用户购物车是否为空
        if (CollectionUtils.isNotEmpty(userCart)){
            //如果购物车不为空,开始遍历购物车的每个商品信息并进行封装,实际上这里的意思是每个购物车对象Cart只能放一个商品,最后总的购物车由CartVo展示
            for (Cart cartItem:userCart
                 ) {
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());     //先将上面三个信息塞入购物车产品抽象对象，因为不需要判断产品的库存信息等
                //开始判断产品的库存信息等，先查询一波产品
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());  //这波资料不像上面三个一样，而是需要从数据库中拿到然后塞进去
                    //开始判断库存
                    int buyLimitCount=0;
                    if (product.getStock()>=cartItem.getQuantity()){
                        //如果商品库存大于等于购物车内该产品对象累计的产品数,库存充足允许该商品的累计数,这里不需要更新库存.
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.LIMT_NUM_SUCCESS); //不需要限制
                    }else {
                        //库存不足
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.LIMT_NUM_FAIL); //需要限制购物车的商品的个数成库存数
                        //更新购物车商品的有效库存(选择数)
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartForQuantity.setId(cartItem.getId());
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    //设置限制数
                    cartProductVo.setQuantity(buyLimitCount);
                    //开始计算单个商品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
            //如果已经勾选增加到购物车总价中
                if (cartItem.getChecked()==CartCode.CHECKED.getCode()){
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }

        }
        //开始封装总体购物车
        cartVo.setCartTotalPrice(cartTotalPrice); //总价
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","https://cybimage.oss-cn-shenzhen.aliyuncs.com/")); //图片主路径
       cartVo.setAllChecked(getAllCheckedStatus(userId)); //是否全选
       cartVo.setCartProductVoList(cartProductVoList); //购物车产品抽象对象结果集
        return cartVo;
    }
    private boolean getAllCheckedStatus(Integer userId){
        if (userId==null){
            return false;
        }
        Cart cart=new Cart();
        cart.setUserId(userId);
        cart.setChecked(CartCode.UN_CHECKED.getCode());
       return CollectionUtils.isEmpty(cartMapper.select(cart));
    }
    private Cart getOneCart(Integer userId,Integer productId){
        Cart cart=new Cart();
        cart.setProductId(productId);
        cart.setUserId(userId);
       return cartMapper.selectOne(cart);
    }
}
