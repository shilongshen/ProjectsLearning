package com.example.miaosha.error;

import com.example.miaosha.response.CommonReturnType;

public interface CommonError {
    int getErrorCode();
    String getErrorMsg();
    CommonError setErrorMsg(String errMsg);
}
