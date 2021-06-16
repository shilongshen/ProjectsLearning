package com.example.miaosha.service.impl;

import com.example.miaosha.dao.PromoDOMapper;
import com.example.miaosha.dataobject.PromoDO;
import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.service.ItemService;
import com.example.miaosha.service.PromoService;
import com.example.miaosha.service.UserService;
import com.example.miaosha.service.model.ItemModel;
import com.example.miaosha.service.model.PromoModel;
import com.example.miaosha.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;


    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
//        通过itemId获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

//        将dataobject转换为model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if (promoModel == null) {
            return null;
        }
//        判断当前时间是否秒杀活动即将开始或正在开始
        DateTime now = new DateTime();

        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);//还未开始，开始时间在现在时间的后面
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);//已经结束，结束时间在现在时间的前面
        } else {
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    @Override
    public void publishPromo(Integer promoId) {
//        通过活动id获取活动
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if (promoDO == null || promoDO.getItemId() == null || promoDO.getItemId().intValue() == 0) {
            return;
        }
//        获取有活动的商品
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
//        将库存同步到redis内
        redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());

//        将秒杀大闸限制数字设置到redis中
        redisTemplate.opsForValue().set("promo_door_count_" + promoId, itemModel.getStock() * 3);

    }

    @Override
    public String generateStringKillToken(Integer promoId, Integer itemId, Integer userId) throws BusinessException {
        //判断是否库存已售罄，若对应的售罄key存在，则直接返回下单失败
        if (redisTemplate.hasKey("promo_item_stock_" + itemId)) {
            return null;
        }

        //promoId获得promoDo
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        //dataobject-->model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if (promoModel == null) {
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);//还未开始，开始时间在现在时间的后面
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);//已经结束，结束时间在现在时间的前面
        } else {
            promoModel.setStatus(2);//2表示进行中
        }

        //如果不是正在进行秒杀活动，就不生成令牌
        if (promoModel.getStatus() != 2) {
            return null;
        }
        //校验item是否存在
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if (itemModel == null) {
            return null;
        }
        //校验用户是否存在
        UserModel userModel = userService.getUserByIdInCache(userId);
        if (userModel == null) {
            return null;
        }

        //获取秒杀大闸的count数量,减1
        Long result = redisTemplate.opsForValue().increment("promo_door_count_" + promoId, -1);
        if (result < 0) {
            return null;
        }

        //生成令牌，并设置在redis缓存中，并设置过期时间！
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("promo_token_" + promoId + "_userid_" + userId + "_item_id" + itemId, token);
        redisTemplate.expire("promo_token_" + promoId, 5, TimeUnit.MINUTES);
        return token;
    }

    private PromoModel convertFromDataObject(PromoDO promoDO) {
        if (promoDO == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setPromoItemPrice(BigDecimal.valueOf(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }

}
