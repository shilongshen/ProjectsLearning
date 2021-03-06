package com.example.miaosha.dao;

import com.example.miaosha.dataobject.ItemStockDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItemStockDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed May 12 22:24:21 CST 2021
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed May 12 22:24:21 CST 2021
     */
    int insert(ItemStockDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed May 12 22:24:21 CST 2021
     */
    int insertSelective(ItemStockDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed May 12 22:24:21 CST 2021
     */
    ItemStockDO selectByPrimaryKey(Integer id);


    //<!--通过itemId获得用户库存  -->
    ItemStockDO selectByItemId(Integer itemId);


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed May 12 22:24:21 CST 2021
     */
    int updateByPrimaryKeySelective(ItemStockDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed May 12 22:24:21 CST 2021
     */
    int updateByPrimaryKey(ItemStockDO record);

    //     <update id="decreaseStock">
    int decreaseStock(@Param("itemId") Integer itemId, @Param("amount") Integer amount);
}