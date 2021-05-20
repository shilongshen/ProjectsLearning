package com.example.miaosha.service;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.service.model.OrderModel;

public interface OrderService {
    //    创建交易订单，需要用户名，商品id,以及购买数量
//   使用 1.通过前端url上传过来秒杀活动的id，然后下单接口内校验对应的id是否属于对应商品且活动已开始
//    2.直接在下单接口内判断对应的商品是否存在秒杀活动，如果存在进行中的秒杀活动则以秒杀价格下单
    OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException;
//    使用1的原因：
//    首先考虑业务逻辑上的模型可扩展性，一个商品可能会存在于多个秒杀活动之内。
//    需要通过前端用户的一个访问路径来判断用户从哪一个秒杀入口下单
//    其次如果在订单的接口中还要判断秒杀活动的领域模型的话，就相当于每一个平销的商品还要进行活动信息的查询，这回降低下单性能
//
}
