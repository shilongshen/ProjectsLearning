package com.example.miaosha.controller.viewobject;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Data
@ToString
public class ItemVO {
    private int id;

    //    商品名

    private String title;

    //    商品价格

    private BigDecimal price;

    //    商品的库存

    private Integer stock;

    //    商品的描述

    private String description;

    //    商品的销量
    private Integer sales;


    //    商品描述图片的url

    private String imgUrl;

    //记录商品是否在秒杀活动中，以及对应的状态，0表示没有秒杀活动，1表示秒杀活动待开始，2表示秒杀活动正在进行，3表示活动已结束
    private Integer promoStatus;
    //    秒杀活动价格
    private BigDecimal promoPrice;
    //    秒杀活动id
    private Integer promoId;
    //    秒杀活动开始时间
    private String startDate;
}
