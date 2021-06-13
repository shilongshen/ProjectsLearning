package com.example.miaosha.service;

import com.example.miaosha.error.BusinessException;
import com.example.miaosha.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    //    创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //    商品列表浏览
    List<ItemModel> listItem();

    //    商品详情浏览
    ItemModel getItemById(Integer id);

    //    item以及promo model缓存模型,在缓存中通过id获取商品
    ItemModel getItemByIdInCache(Integer id);

    //    库存扣减,根据商品id和商品购买数量扣减商品库存
    boolean decreaseStock(Integer itemId, Integer amount);

    //    库存回补
    boolean increaseStock(Integer itemId, Integer amount);

    //异步更新库存
    boolean asyncDecreaseStock(Integer itemId, Integer amount);

    //    商品销量增加
    void insreaseSales(Integer itemId, Integer amount);

    //初始化库存流水
    //作用：StockLog生成后再进行订单创建
    String initStockLog(Integer itemId, Integer amount);
}
