package com.example.miaosha.service;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.service.model.OrderModel;

public interface OrderService {
    //    创建交易订单，需要用户名，商品id,以及购买数量
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;
}
