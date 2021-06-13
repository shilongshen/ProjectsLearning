package com.example.miaosha.service.impl;

import com.example.miaosha.dao.ItemDOMapper;
import com.example.miaosha.dao.ItemStockDOMapper;
import com.example.miaosha.dao.StockLogDOMapper;
import com.example.miaosha.dataobject.ItemDO;
import com.example.miaosha.dataobject.ItemStockDO;
import com.example.miaosha.dataobject.StockLogDO;
import com.example.miaosha.error.BusinessException;
import com.example.miaosha.error.EmBusinessError;
import com.example.miaosha.mq.MqProducer;
import com.example.miaosha.service.ItemService;
import com.example.miaosha.service.PromoService;
import com.example.miaosha.service.model.ItemModel;
import com.example.miaosha.service.model.PromoModel;
import com.example.miaosha.validator.ValidationResult;
import com.example.miaosha.validator.ValidatorImpl;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private ValidatorImpl validator;
    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @Autowired
    private PromoService promoService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    private ItemDO convertItemDoFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());//类型转换为double
        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    @Override
    @Transactional

    /**
     * 创建商品、进行校验、并将其写入数据库中
     * */
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
//        校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

//        转换ItemModel为dataobject
        ItemDO itemDO = this.convertItemDoFromItemModel(itemModel);


//        写入数据库
        itemDOMapper.insertSelective(itemDO);//插入item表中
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);//插入item_stock

        itemStockDOMapper.insertSelective(itemStockDO);

//        返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();//有imgurl
        List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemDO, itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());//没有imgurl

        return itemModelList;
    }


    /**
     * 商品详情浏览
     */
    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);//通过主键获取item表中的商品数据
        if (itemDO == null) {
            return null;
        }
//        操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

//        将dataobject转换为model
        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);

//      获取活动商品信息
//        通过itemId获得promoModel
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
//        判断该商品是否存在促销活动，判断的依据为promoModel不为空且promoModel的status不为3
        if (promoModel != null && promoModel.getStatus() != 3) {
            //将itemModel中的promoModel进行设置
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }


    //    item以及promo model缓存模型,在缓存中通过id获取商品
    @Override
    public ItemModel getItemByIdInCache(Integer id) {
//        在redis缓存中查询是否有对应的商品
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_" + id);
        if (itemModel == null) {
//            如果没有在数据库中查找，并将其设置到redis缓存中
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set("item_validate_" + id, itemModel);
            redisTemplate.expire("item_validate_" + id, 20, TimeUnit.MINUTES);
        }
        return itemModel;
    }


    //    库存扣减,根据商品id和商品购买数量扣减商品库存
    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
//        返回值为影响的条目数，如果sql语句执行失败，返回值为0
//        int affectRow = itemStockDOMapper.decreaseStock(itemId, amount);
//        更新活动商品（减少）redis中的库存
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);
//        result表示完成减库存操作后的数字
        if (result > 0) {
//            更新库存成功
//            如果消息发送成功，返回true
            return true;
        } else if (result == 0) {
//打上库存已经售罄的标识
            redisTemplate.opsForValue().set("promo_item_stock_" + itemId,"true");
            return  true;
        } else {
//            更新库存失败
//            有可能amount数量太多了，将库存回滚
            increaseStock(itemId, amount);
            return false;
        }
    }

    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
        return true;
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        return mqProducer.asyncReduceStock(itemId, amount);
    }

    @Override
    @Transactional
//    考虑落单成功，商品的销量就增加
    public void insreaseSales(Integer itemId, Integer amount) {
        itemDOMapper.insreaseSales(itemId, amount);
    }

    @Override
    @Transactional
//    初始化对应的库存流水
    //1表示初始状态，2表示下单成功，3表示下单回滚
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
        stockLogDO.setStatus(1);

        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogDO.getStockLogId();
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setImgUrl(itemDO.getImgurl());
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());

        return itemModel;
    }
}
