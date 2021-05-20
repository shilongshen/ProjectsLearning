package com.example.miaosha.service.model;


import java.math.BigDecimal;

//用户下单的交易模型
public class OrderModel {
    //    交易订单号，是有对应的生成规则的，设置为主键，但是不设计为自增
    private String id;
    //    购买的用户的的id
    private Integer userId;
    //    购买的商品的id
    private Integer itemId;
    //  购买商品的单价,若promoId非空，则表示秒杀商品价格
    private BigDecimal itemPrice;
    //购买数量
    private Integer amount;
    //    购买总金额 = 购买数量*购买商品的单价，，，若promoId非空，则表示秒杀购买总金额
    private BigDecimal orderPrice;


    //    若非空，表示以秒杀商品方式下单
    private Integer promoId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }
}
