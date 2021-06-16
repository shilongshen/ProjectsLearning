package com.example.miaosha.service;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.service.model.PromoModel;

public interface PromoService {
    //    根据itemId获取即将进行或正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);

    //    活动发布
    void publishPromo(Integer promoId);

    //    生成秒杀用的令牌
    String generateStringKillToken(Integer promoId, Integer itemId, Integer userId) throws BusinessException;

}
