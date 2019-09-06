package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.RoleCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public ServerResponse<User> login(String username, String password) {
        ServerResponse<String> serverResponse = checkValid(username, Const.USERNAME);
        if (serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        User user=new User();
        user.setUsername(username);
        user.setPassword(MD5Util.encodeToHex(Const.SALT + password));
        user=userMapper.selectOne(user);
         if (user==null){
             return ServerResponse.createByErrorMessage("账户密码错误");
         }
         user.setPassword(null);  //将密码设置为空后返回提高安全性
         user.setAnswer(null);
         return ServerResponse.createBySuccess("登陆成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> serverResponse = checkValid(user.getUsername(), Const.USERNAME);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        serverResponse = checkValid(user.getEmail(), Const.EMAIL);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        user.setPassword(MD5Util.encodeToHex(Const.SALT + user.getPassword()));   //加密用户密码
        user.setRole(RoleCode.ROLE_USER.getCode());  //分配角色
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        int insert = userMapper.insert(user);
        if (insert==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (type.equals(Const.USERNAME)){
            User user=new User();
            user.setUsername(str);
            user=userMapper.selectOne(user);
            if (user!=null){
                return ServerResponse.createByErrorMessage("用户名已存在");
            }

        }
        if (type.equals(Const.EMAIL)){
            User user=new User();
            user.setEmail(str);
            user=userMapper.selectOne(user);
            if (user!=null){
                return ServerResponse.createByErrorMessage("邮箱已存在");
            }
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> getQuestion(String username) {
        ServerResponse<String> serverResponse = checkValid(username, Const.USERNAME);
        if (serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
         User user=new User();
        user.setUsername(username);
        user=userMapper.selectOne(user);
        if (StringUtils.isBlank(user.getQuestion())){
            return ServerResponse.createByErrorMessage("用户未设置问题");
        }
         return ServerResponse.createBySuccess(user.getQuestion());
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        User user=new User();
        user.setUsername(username);
        user.setQuestion(question);
        user.setAnswer(answer);
        user=userMapper.selectOne(user);
        if (user!=null) {
            String uuid = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(username,uuid,12, TimeUnit.HOURS); //12小时的Token
            return ServerResponse.createBySuccess(uuid);
        }
        return ServerResponse.createByErrorMessage("用户问题答案错误");
    }

    @Override
    public ServerResponse<String> resetPassword(String username, String passwordNew, String token) {
        if (StringUtils.isBlank(redisTemplate.opsForValue().get(username))){
            return ServerResponse.createByErrorMessage("token已经失效");
        }
        if (token.equals(redisTemplate.opsForValue().get(username))){
            passwordNew=MD5Util.encodeToHex(Const.SALT+passwordNew);
            int result = userMapper.resetPassword(username, passwordNew);
            if (result==1){
               return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误,请重新获取");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        User userNew = new User();
        userNew.setUsername(user.getUsername());
        userNew.setPassword(MD5Util.encodeToHex(Const.SALT+passwordOld));
        userNew = userMapper.selectOne(userNew);
        if (userNew==null){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        passwordNew=MD5Util.encodeToHex(Const.SALT+passwordNew);
        int result = userMapper.resetPassword(user.getUsername(), passwordNew);
        if (result==1){
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> updateInformation(User user) {
        //邮箱不能重复
            int resultCount = userMapper.checkEmailById(user.getId(), user.getEmail());
            if (resultCount > 0) {
                return ServerResponse.createByErrorMessage("邮箱已存在,请更换email重新更新");
            }
        int result = userMapper.updateByPrimaryKeySelective(user);
        if (result>0){
          return ServerResponse.createBySuccessMessage("更新个人信息成功");
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }


}
