package com.example.miaosha.response;

import lombok.Data;
import lombok.ToString;

public class CommonReturnType {
//    表明对应请求的返回处理结果，status为“success”或“fail”
    private String status;

//  如果status为“success”，则data内返回前端需要的json数据
//  如果status为“fail”，则data内使用通用的错误码格式
    private Object data;

//    定义一个通用的创建方法
//    当controller完成了处理，调用对应的create方法 ，
//    如果说create方法不带有status，那么对应的status就是“success”
//    然后创建对应的CommonReturnType，并且将status和data进行设置，然后将CommonReturnType返回
    public  static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }
    public  static CommonReturnType create(Object result,String status){
        CommonReturnType type=new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
