package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.RoleCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/orderManager")
public class OrderManagerController {
     @Autowired
     private OrderService orderService;
    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(defaultValue = "1")Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if (user.getRole()!= RoleCode.ROLE_ADMIN.getCode()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }

       return orderService.findAll(pageNum, pageSize);
    }
    @RequestMapping(value = "/search.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderVo> search(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if (user.getRole()!= RoleCode.ROLE_ADMIN.getCode()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }
       return orderService.searchByOrderNo(orderNo);
    }
    @RequestMapping(value = "/send_good.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse sendGoods(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if (user.getRole()!= RoleCode.ROLE_ADMIN.getCode()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }
        return orderService.updateSendStatus(orderNo);
    }
}
