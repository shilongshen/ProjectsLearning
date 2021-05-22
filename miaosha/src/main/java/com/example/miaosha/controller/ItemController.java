package com.example.miaosha.controller;

import com.example.miaosha.controller.viewobject.ItemVO;
import com.example.miaosha.error.BusinessException;
import com.example.miaosha.response.CommonReturnType;
import com.example.miaosha.service.ItemService;
import com.example.miaosha.service.model.ItemModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/item")
//@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//完成所有spring boot对应返回web请求中加上跨域ajlohead对应的标签
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {
    @Autowired
    private ItemService itemService;
    /**
     * 创建商品的controller
     */
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "description") String description,
//                                       @RequestParam(name = "sales") Integer sales,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
//            封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
//        itemModel.setSales(sales);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = converVOFromModel(itemModelForReturn);
        return CommonReturnType.create(itemVO);
    }
    //    viewObject（展示给用户的属性）和model层的定义是不一样的
    private ItemVO converVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);//复制后ItemVO为null？

        if (itemModel.getPromoModel() != null) {
//如果有正在进行的或即将进行的秒杀活动
//            将待显示的活动信息进行相应的设置
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getItemId());
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }


    /**
     * 商品详情页浏览
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = converVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    //    商品列表页面浏览
    //    按照销量进行降序排列
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem() {
        List<ItemModel> itemModelList = itemService.listItem();

        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.converVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

}
