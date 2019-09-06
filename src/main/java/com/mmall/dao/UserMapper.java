package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
* Created by Mybatis Generator on 2019/08/15
*/
@Repository
public interface UserMapper extends Mapper<User>{
    int resetPassword(@Param(value = "username") String username, @Param(value = "password")String password);
    int checkEmailById(@Param(value = "id")Integer userId,@Param(value = "email") String email);
}