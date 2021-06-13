package com.example.miaosha.service.impl;

import com.example.miaosha.dao.OrderDOMapper;
import com.example.miaosha.dao.SequenceDOMapper;
import com.example.miaosha.dao.StockLogDOMapper;
import com.example.miaosha.dataobject.OrderDO;
import com.example.miaosha.dataobject.SequenceDO;
import com.example.miaosha.dataobject.StockLogDO;
import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.service.ItemService;
import com.example.miaosha.service.OrderService;
import com.example.miaosha.service.UserService;
import com.example.miaosha.service.model.ItemModel;
import com.example.miaosha.service.model.OrderModel;
import com.example.miaosha.service.model.UserModel;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private SequenceDOMapper sequenceDOMapper;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;


    //    根据用户名，商品id,以及购买数量创建交易订单
    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BusinessException {
        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确,以及校验活动信息
//        ItemModel itemModel = itemService.getItemById(itemId);//通过itemId获取itemModel
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);//在redis缓存中通过itemId获取itemModel

        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品不存在");
        }

        UserModel userModel = userService.getUserByIdInCache(userId);//查询用户信息
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户不存在");
        }

        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不正确");
        }
        //校验活动信息
        if (promoId != null) {
            //(1)校验对应活动是否存在这个使用商品
            //如果itemModel中PromoModel为null,说明该商品没有活动，
            if (promoId.intValue() != itemModel.getPromoModel().getItemId()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            }
            //(2)活动还未开始
            else if (itemModel.getPromoModel().getStatus() != 2) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            }
        }

        //2.落单减库存
        Boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //3.订单入库
//        设置订单（orderModel）的各个属性,
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if (promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

//生成交易订单号
        orderModel.setId(generateOrderNo());

        OrderDO orderDO = convertFromOrderModel(orderModel);//将orderModel转换为orderDO
        orderDOMapper.insertSelective(orderDO);//将orderDO存入数据库表中
//加上商品的销量
        itemService.insreaseSales(itemId, amount);

//设置库存流水状态为成功
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if (stockLogDO == null) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        stockLogDO.setStatus(2);
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);

//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//            @SneakyThrows
//            @Override
////            在最近一个Transactional成功commit后再执行方法
//            public void afterCommit() {
//                //当该事务中的前面操作全部成功后才发送异步消息更新数据库中的库存
//                //异步更新库存
//                boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
////                但是一旦异步消息发送失败就没有办法回滚库存了
////        如果消息发送失败，将库存进行回滚,抛出异常
////                if (!mqResult) {
////                    itemService.increaseStock(itemId,amount);
////                    throw new BusinessException(EmBusinessError.MQ_SEND_FAIL);
////                }
//            }
//        });

        //4.返回前端
        return orderModel;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo() {
        StringBuilder stringBuilder = new StringBuilder();
        //1.假设订单号为16位，前8位为年月日，
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);

        // 2.中间6位为自增序列，
//        获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
//        按照step增加CurrentValue
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
//        写入数据库
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
//        将当前sequence转换为string
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);//不足6位用0拼接
        }
        stringBuilder.append(sequenceStr);

        // 3.最后两位为分库分表位,00-99,,暂时为00
        stringBuilder.append("00");
        return stringBuilder.toString();

    }


    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
//        将BigDecimal转换为double
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());

        return orderDO;
    }

}
