package com.mmall.controller;

import com.mmall.annotation.ManagerAnnotation;
import com.mmall.common.Const;
import com.mmall.common.ManagerCode;
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
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>  login(String username, String password, HttpSession session){
        if (StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return ServerResponse.createByErrorMessage("非法登录");
        }
        ServerResponse<User> userServerResponse = userService.login(username, password);
        if (userServerResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,userServerResponse.getData());
        }
        return userServerResponse;
    }
    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        if (user==null){
            return ServerResponse.createByErrorMessage("非法注册");
        }
        return userService.register(user);
    }
    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    @RequestMapping(value = "/check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        if (StringUtils.isBlank(str)||StringUtils.isBlank(type)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return userService.checkValid(str,type);
    }
    @RequestMapping(value = "/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        user.setPassword(null);
        user.setAnswer(null);
        return ServerResponse.createBySuccess(user);
    }
    @RequestMapping(value = "/forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        if (StringUtils.isBlank(username)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return userService.getQuestion(username);
    }
    @RequestMapping(value = "/forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
            if (StringUtils.isBlank(username)||StringUtils.isBlank(question)||StringUtils.isBlank(answer)){
                ServerResponse.createByErrorMessage("参数错误");
            }
          return   userService.checkAnswer(username, question, answer);
    }
    @RequestMapping(value = "/forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String token){
        if (StringUtils.isBlank(username)||StringUtils.isBlank(passwordNew)||StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return userService.resetPassword(username, passwordNew, token);
    }
    @RequestMapping(value = "/reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<String> restPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (StringUtils.isBlank(passwordNew)||StringUtils.isBlank(passwordOld)){
            return ServerResponse.createByErrorMessage("参数有误");
        }
        return userService.resetPassword(passwordOld,passwordNew,user);
    }
    @RequestMapping(value = "/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<String> updateInformation(HttpSession session,User user){
        User curretUser =(User) session.getAttribute(Const.CURRENT_USER);
        //能修改的信息进行封装,防止横向越权
        curretUser.setAnswer(user.getAnswer());
        curretUser.setQuestion(user.getQuestion());
        curretUser.setEmail(user.getEmail());
        curretUser.setPhone(user.getPhone());
        ServerResponse<String> serverResponse = userService.updateInformation(curretUser);
        if (serverResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,curretUser);
        }
        return serverResponse;
    }
    @RequestMapping(value = "/get_information.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<User> loginAndGetInformation(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        user.setPassword(null);
        user.setAnswer(null);
        return ServerResponse.createBySuccess(user);
    }

}
