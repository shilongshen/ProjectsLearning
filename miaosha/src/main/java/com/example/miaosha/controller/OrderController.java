package com.example.miaosha.controller;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.mq.MqProducer;
import com.example.miaosha.response.CommonReturnType;
import com.example.miaosha.service.ItemService;
import com.example.miaosha.service.OrderService;
import com.example.miaosha.service.model.OrderModel;
import com.example.miaosha.service.model.UserModel;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private ItemService itemService;


    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "amount") Integer amount) throws BusinessException {

//        在UserController将登录凭证加入到用户登录成功的session内，在Session中设置IS_LOGIN，LOGIN_USER
//        因此只需要从用户的Session中获取道对应的用户信息即可,

        //根据IS_LOGIN判断用户是否登录
//        现在不用IS_LOGIN进行判断了
//        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
//        获取tokenit
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (token == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
//通过token获取userModel
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);


//        if (isLogin == null || !isLogin) {
//            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
//        }
        //获取用户的登录信息userModel  LOGIN_USER
//        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户登录过期，userModel等于null");
        }

//判断是否库存已售罄，若对应的售罄key存在，则直接返回下单失败
        if (redisTemplate.hasKey("promo_item_stock_" + itemId)) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
//加入库存流水init状态  -->下单之前初始化一条库存流水,然后库存流水就可以用于追踪异步扣减库存的消息
        String stockLogId = itemService.initStockLog(itemId, amount);


//        再去完成对应的下单事务型消息机制
//创建订单,只有用户登录了才能够进行下单，用户的登录信息是在当前Session中获取的
        //OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);
        boolean transactionAsyncReduceStock = mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId);
        if (!transactionAsyncReduceStock) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
        }

        return CommonReturnType.create(null);
    }
//
}
