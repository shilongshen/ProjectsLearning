package com.example.miaosha.controller;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.mq.MqProducer;
import com.example.miaosha.response.CommonReturnType;
import com.example.miaosha.service.ItemService;
import com.example.miaosha.service.OrderService;
import com.example.miaosha.service.PromoService;
import com.example.miaosha.service.model.OrderModel;
import com.example.miaosha.service.model.UserModel;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

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
    @Autowired
    private PromoService promoService;
    private ExecutorService executorService;

    @PostConstruct
    public void init() {//创建线程池
        executorService = Executors.newFixedThreadPool(20);
    }

    //生成秒杀令牌
    @RequestMapping(value = "/generatetoken", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType generatetoken(@RequestParam(name = "itemId") Integer itemId,
                                          @RequestParam(name = "promoId", required = true) Integer promoId
    ) throws BusinessException {
        //根据token获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (token == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
//通过token获取userModel,获取用户登录信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户登录过期，userModel等于null");
        }

        //获取秒杀访问令牌
        String promoToken = promoService.generateStringKillToken(promoId, itemId, userModel.getId());
        if (promoToken == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "生成令牌失败");
        }
        return CommonReturnType.create(promoToken);

    }

    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoToken", required = false) String promoToken) throws BusinessException {

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

        //校验秒杀秒杀令牌是否正确,与redis中的promoToken进行比较
        if (promoId != null) {
            String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_userid_" + userModel.getId() + "_item_id" + itemId);
            if (!StringUtils.equals(promoToken, inRedisPromoToken)) {
                //如果不等就抛异常
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀令牌校验失败");
            }
        }

        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //加入库存流水init状态  -->下单之前初始化一条库存流水,然后库存流水就可以用于追踪异步扣减库存的消息
                String stockLogId = itemService.initStockLog(itemId, amount);
//        再去完成对应的下单事务型消息机制
//创建订单,只有用户登录了才能够进行下单，用户的登录信息是在当前Session中获取的
                //OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);
                boolean transactionAsyncReduceStock = mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId);
                if (!transactionAsyncReduceStock) {
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
                }
                return null;
            }

        });

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }

        return CommonReturnType.create(null);
    }
//
}
