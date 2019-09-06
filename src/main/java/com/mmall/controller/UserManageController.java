package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.RoleCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/userManager")
public class UserManageController {
    @Autowired
    private UserService userService;
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        if (StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return ServerResponse.createByErrorMessage("非法登录");
        }
        ServerResponse<User> userServerResponse = userService.login(username, password);
        if (userServerResponse.isSuccess()){
            User user = userServerResponse.getData();
            if (user.getRole()==RoleCode.ROLE_ADMIN.getCode()){
                session.setAttribute(Const.CURRENT_USER, user);
               return userServerResponse;
            }else {
                return ServerResponse.createByErrorMessage("不是管理员无法登录");
            }
        }
        return userServerResponse;
    }
}
