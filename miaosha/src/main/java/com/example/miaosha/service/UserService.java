package com.example.miaosha.service;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.service.model.UserModel;
import org.apache.catalina.User;

public interface UserService {
    //    通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);

//    完成用户注册
    void register(UserModel userModel) throws BusinessException;

//    用户登录校验
    /**
    * telphone：用户注册手机号
     * password：用户加密后的密码
    * */
    UserModel vaildateLogin(String telphone,String encrptPassword) throws BusinessException;
}
