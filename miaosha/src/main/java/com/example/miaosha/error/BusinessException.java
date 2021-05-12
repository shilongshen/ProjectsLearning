package com.example.miaosha.error;

/**
 * 包装器业务异常类实现
 * BusinessException和EmBusinessError都共同继承了CommonError
 * 所以外部不仅可以通过new BusinessException而且可以通过new EmBusinessError
 * 都可以获得code和errMsg的组装定义
 * 并且需要实现setErrorMsg，用于将errMsg覆盖掉
 * */
public class BusinessException extends Exception implements CommonError{
    private CommonError commonError;

//    直接接收EmBusinessError的传参用于构造业务异常
    public BusinessException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }
//    接收自定义errMsg的方式构造业务异常


    //接收自定义errMsg的方式构造业务异常
    public BusinessException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrorMsg(errMsg);
    }

    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMsg() {
        return this.commonError.getErrorMsg();
    }

    @Override
    public CommonError setErrorMsg(String errMsg) {
         this.commonError.setErrorMsg(errMsg);
        return this;
    }
}
