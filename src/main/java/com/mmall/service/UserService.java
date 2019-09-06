package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface UserService {



    ServerResponse<User> login(String username,String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> getQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> resetPassword(String username,String passwordNew,String token);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<String> updateInformation(User user);
}
