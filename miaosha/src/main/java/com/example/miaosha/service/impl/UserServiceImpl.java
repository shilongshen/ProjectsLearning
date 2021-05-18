package com.example.miaosha.service.impl;

import com.example.miaosha.dao.UserDOMapper;
import com.example.miaosha.dao.userPasswordDOMapper;
import com.example.miaosha.dataobject.UserDO;
import com.example.miaosha.dataobject.userPasswordDO;
import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.service.UserService;
import com.example.miaosha.service.model.UserModel;
import com.example.miaosha.validator.ValidationResult;
import com.example.miaosha.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private userPasswordDOMapper userPasswordDOMapper;
    @Autowired
    private ValidatorImpl validator;

    /**
     * 用户查询
     * 通过用户ID获取用户对象的方法
     * */
    @Override
    public UserModel getUserById(Integer id) {
//        调用UserDOMapper获取对应的用户UserDO
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);

        if (userDO == null) {
            return null;
        }

//        selectByPrimaryKey.getId():获取用户名
//        通过用户名获取用户密码
        userPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO, userPasswordDO);
    }

    /**
     * 完成用户注册
     */
    @Override
    @Transactional//事务
    public void register(UserModel userModel) throws BusinessException {
//控制判断，如果为空直接抛异常
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "未创建用户");
        }
//        用户注册校验
//        if (StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender() == null
//                || userModel.getAge() == null
//                || StringUtils.isEmpty(userModel.getTelphone())) {
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息填写格式有误");
//        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

//        实现UserModel->UserDao
        UserDO userDO = convertFromModel(userModel);
//        将userDO写入数据库
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已被注册");
        }

//user_password表中的user_id就等于user_info表中的id，取出user_info中的自增主键，并赋值给userModel，
        userModel.setId(userDO.getId());//关键是id为null

//        实现UserModel->userPasswordDO
        userPasswordDO userPasswordDO = convertuserPasswordDOFromModel(userModel);
//        将userPasswordDO写入数据库中
        userPasswordDOMapper.insertSelective(userPasswordDO);

//
// 为什么要使用insertSelective而不是insert?
//insertSelective相对于insert方法，不会覆盖掉数据库的默认值
//

    }

    /**
     * 用户登录校验
     */
    @Override
    public UserModel vaildateLogin(String telphone, String encrptPassword) throws BusinessException {
//    通过用户的手机号获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        //如果查找不到对应的信息，直接抛出异常
        if (userDO == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        userPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        //通过userDO和userPasswordDO获取userModel
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);


//    比对用户信息内加密的用户密码是否和传输进来的密码相匹配
        //如果不匹配直接抛出异常
        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
//        如果用户登录成功，返回UserModel
        return userModel;

    }

    //实现UserModel->UserDao
    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
//        将userModel的属性复制到userDO
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    //实现UserModel->userPasswordDO
    private userPasswordDO convertuserPasswordDOFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        userPasswordDO userPasswordDO = new userPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, userPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
//        将userDO的属性复制到userModel中
        BeanUtils.copyProperties(userDO, userModel);
        if (userPasswordDO != null) {
//        将userModel中的EncrptPassword通过getEncrptPassword获得并通过set方法进行设置
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;


    }
}
