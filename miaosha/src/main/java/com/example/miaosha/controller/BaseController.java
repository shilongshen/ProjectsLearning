package com.example.miaosha.controller;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";

//    异常处理模块
    //    定义exceptionhandler解决未被controller层吸收的exception异常
//    就是之前的抛出的BusinessException可以交给这里来处理，或者是其他异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)//如果出现异常，会捕获这个异常，返回的状态码仍然是OK，并返回一个{errCode:{},errMsg:{}}格式的提示
    @ResponseBody//返回json
    public Object handlerException(HttpServletRequest request, Exception exception){

        Map<String,Object> responseData=new HashMap<>();
        if (exception instanceof BusinessException){
            BusinessException businessException= (BusinessException) exception;
            responseData.put("errCode",businessException.getErrorCode());
            responseData.put("errMsg",businessException.getErrorMsg());
        }else {//如果不是抛出的BusinessException就提示未知错误
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrorMsg());
        }

        return CommonReturnType.create(responseData,"fail");

//        CommonReturnType commonReturnType=new CommonReturnType();
//        commonReturnType.setStatus("fail");
//
//        BusinessException businessException= (BusinessException) exception;
//        Map<String,Object> responseData=new HashMap<>();
//        responseData.put("errCode",businessException.getErrorCode());
//        responseData.put("errMsg",businessException.getErrorMsg());
//
//        commonReturnType.setData(responseData);
//
//        return commonReturnType;

    }
}
