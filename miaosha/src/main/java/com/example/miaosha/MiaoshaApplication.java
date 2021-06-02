package com.example.miaosha;

import com.example.miaosha.dao.UserDOMapper;
import com.example.miaosha.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication(scanBasePackages = {"com.example"})//
@MapperScan("com.example.miaosha.dao")
@Controller

public class MiaoshaApplication {

    @Autowired
    private UserDOMapper userDOMapper;


    @RequestMapping("/")
    public String home() {
        return "index";
    }

    public static void main(String[] args) {
//        System.out.println("hello world!");
        SpringApplication.run(MiaoshaApplication.class, args);
    }

}
