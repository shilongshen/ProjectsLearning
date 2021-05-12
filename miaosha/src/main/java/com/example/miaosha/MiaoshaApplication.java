package com.example.miaosha;

import com.example.miaosha.dao.UserDOMapper;
import com.example.miaosha.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.example.miaosha"})//
@MapperScan("com.example.miaosha.dao")
@RestController

public class MiaoshaApplication {

    @Autowired
    private UserDOMapper userDOMapper;


    @RequestMapping("/")
    public String home() {
        UserDO selectByPrimaryKey = userDOMapper.selectByPrimaryKey(1);
        if (selectByPrimaryKey == null) {
            return "用户对象不存在";
        }
        return selectByPrimaryKey.getName();
    }


    public static void main(String[] args) {
//        System.out.println("hello world!");
        SpringApplication.run(MiaoshaApplication.class, args);
    }

}
