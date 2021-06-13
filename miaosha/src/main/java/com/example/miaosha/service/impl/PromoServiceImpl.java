package com.example.miaosha.service.impl;

import com.example.miaosha.dao.PromoDOMapper;
import com.example.miaosha.dataobject.PromoDO;
import com.example.miaosha.service.ItemService;
import com.example.miaosha.service.PromoService;
import com.example.miaosha.service.model.ItemModel;
import com.example.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private RedisTemplate redisTemplate;


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
        if (promoDO==null||promoDO.getItemId() == null || promoDO.getItemId().intValue() == 0) {
            return;
        }
//        获取有活动的商品
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
//        将库存同步到redis内
        redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());

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
