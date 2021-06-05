package com.example.miaosha.controller;

import com.alibaba.druid.util.StringUtils;
import com.example.miaosha.controller.viewobject.UserVO;
import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.CommonError;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.response.CommonReturnType;
import com.example.miaosha.service.UserService;
import com.example.miaosha.service.model.UserModel;
import org.apache.catalina.User;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
//import sun.misc.BASE64Encoder;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller("user")
@RequestMapping("/user")
//@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//完成所有spring boot对应返回web请求中加上跨域ajlohead对应的标签
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;


    //    用户登录接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone
            , @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

//    入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telphone)
                || org.apache.commons.lang3.StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号和密码不能为空");
        }


//用户登录服务
        //       传入的密码为经过加密的密码
        UserModel userModel = userService.vaildateLogin(telphone, this.EncoderByMd5(password));

//     将登录凭证加入到用户登录成功的session内，假设用户是单点的session登录
        //如果用户的session中有IS_LOGIN标识，就将其设置为已经登录成功

//        修改为如果用户登录验证成功将对应的登录信息和登录凭证一起存入redis中
//        生成登录凭证token，UUID
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-", "");

//        建立token和用户登录态之间的联系

        /**
        * 注意新建token后token会存储在redis以及本地浏览器中，注意清除本地浏览器中的token
        * */
        redisTemplate.opsForValue().set(uuidToken, userModel);//redis中uuidToken就是key，userModel就是value这样一来只要redis中存在uuidToken这个key，就惹味userModel存在
        redisTemplate.expire(uuidToken, 1, TimeUnit.HOURS);//设置超时时间为1小时

//        现在不需要IS_LOGIN了
//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
//        //如果用户登录成功，就将userModel放到对应用户的session
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        // 并且返回给前端一个正确的信息


//        下发了token
        return CommonReturnType.create(uuidToken);
    }


    //   用户注册接口
//    用户注册需要：手机号，验证码otp,姓名，性别，年龄
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Byte gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password
    ) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
//        验证手机号和对应的otpCode向符合
//        从Session通过手机号取出验证码
        String InSessionotpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
//        如果输入的验证码和发送的验证码不同，直接抛出异常
        if (!StringUtils.equals(otpCode, InSessionotpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不正确");
        }


//        用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisitMode("byphone");
//        将加密后的password存储在数据库中，使用MD5加密
        userModel.setEncrptPassword(this.EncoderByMd5(password));

//        调用register进行注册
        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    /**
     * 定义MD5加密方式
     */
    public String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        确定一个计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        //        BASE64Encoder base64Encoder = new BASE64Encoder();
//        加密字符串
//        String encode = base64Encoder.encode(str.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64String(str.getBytes());
    }

    //    用户获取otp短信接口
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOpt(@RequestParam(name = "telphone") String telphone) {
        //按照一定的规则生成opt验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);//生成[0,99999)间的一个随机数
        randomInt += 10000;//[0+10000,99999+10000)
        String otpCode = String.valueOf(randomInt);

//    将otp验证码同对应用户的手机号关联,使用HTTP session的方式绑定手机号与otpCode
        httpServletRequest.getSession().setAttribute(telphone, otpCode);


//    将otp验证码通过短信通道发送给用户（省略）
        System.out.println("telphone=" + telphone + "&otpCode=" + otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
//        调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

//        如果获取的对应用户信息不存在,直接抛出一个异常，交由BusinessException处理
        if (userModel == null) {
//            userModel.setEncrptPassword("123");
//            抛出一个USER_NOT_EXIST异常
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

//将核心领域模型用户对象转换为可供前端使用的viewobject
        UserVO userVO = convertFromModel(userModel);

//       返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }


}
