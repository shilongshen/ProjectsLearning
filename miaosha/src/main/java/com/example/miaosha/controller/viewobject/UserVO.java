package com.example.miaosha.controller.viewobject;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserVO {
    private Integer id;
    private String name;
    private Byte gender;
    private  Integer age;
    private String telphone;
    /**
     * 对于前端展示仅仅只需要以上信息，而不需要以下3个信息
     * */
//    private String regisitMode;
//    private Integer thirdPartyId;
//    private String encrptPassword;
}
