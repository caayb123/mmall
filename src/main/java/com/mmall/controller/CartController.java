package com.mmall.controller;

import com.mmall.annotation.ManagerAnnotation;
import com.mmall.common.*;
import com.mmall.pojo.Cart;
import com.mmall.pojo.User;
import com.mmall.service.CartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping(value = "/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @RequestMapping(value = "/list.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> delete(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return cartService.list(user.getId());
    }

    @RequestMapping(value = "/add.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> add(HttpSession session,Integer count,Integer productId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return cartService.add(user.getId(),productId,count);
    }
    @RequestMapping(value = "/update.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> update(HttpSession session,Integer count,Integer productId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return cartService.update(user.getId(),productId,count);
    }
    @RequestMapping(value = "/delete.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> delete(HttpSession session, String productIds){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
       return cartService.delete(user.getId(),productIds);
    }
    @RequestMapping(value = "/allCheck.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> allCheck(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return cartService.updateAllCheckOrUnCheck(CartCode.CHECKED.getCode(),user.getId());
    }
    @RequestMapping(value = "/allUnCheck.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> allUnCheck(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return cartService.updateAllCheckOrUnCheck(CartCode.UN_CHECKED.getCode(),user.getId());
    }
    @RequestMapping(value = "/oneCheck.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> oneCheck(HttpSession session,Integer productId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return cartService.updateOneCheckOrUnCheck(CartCode.CHECKED.getCode(),user.getId(),productId);
    }
    @RequestMapping(value = "/oneUnCheck.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<CartVo> oneUnCheck(HttpSession session, Integer productId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return cartService.updateOneCheckOrUnCheck(CartCode.UN_CHECKED.getCode(),user.getId(),productId);
    }
    @RequestMapping(value = "/get_cart_product_count.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createBySuccess(0);
        }
     return cartService.getCartProductCount(user.getId());
    }


}
