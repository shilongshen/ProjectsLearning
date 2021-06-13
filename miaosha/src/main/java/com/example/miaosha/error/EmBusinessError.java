package com.example.miaosha.error;

import com.example.miaosha.response.CommonReturnType;

/**
 * 枚举常量也可以有自己的方法
* 此时要注意必须在枚举实例的最后一个成员后添加分号，而且必须先定义枚举实例
* */
public enum EmBusinessError implements CommonError {
//    枚举实例
//    通用的错误类型100001
    PARAMETER_VALIDATION_ERROR(100001,"参数不合法"),
//    未知错误类型
    UNKNOWN_ERROR(100002,"未知错误"),

    //    20000开头为用户信息相关错误定义
    USER_NOT_EXIST(200001, "用户不存在"),
    USER_LOGIN_FAIL(200002, "用户手机号或密码不正确"),
    USER_NOT_LOGIN(200003, "用户还未登录"),


//    30000开头为交易信息错误
    STOCK_NOT_ENOUGH(300001,"库存不足"),
    MQ_SEND_FAIL(300002,"库存异步消息发送失败"),
    ;

//    成员变量
    private int code;
    private String errMsg;

    EmBusinessError(int code, String errMsg) {
        this.code = code;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrorMsg(String errMsg) {
//        可以通过定制化的方式改动errMsg
        this.errMsg=errMsg;
        return this;
    }
}
