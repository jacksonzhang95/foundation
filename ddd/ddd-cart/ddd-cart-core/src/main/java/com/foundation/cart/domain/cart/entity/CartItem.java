package com.foundation.cart.domain.cart.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : jacksonz
 * @date : 2022/5/28 16:03
 * @description : 购物车商品信息
 */
@Data
public class CartItem {

    /**
     * 唯一主键
     */
    private CartItemID id;

    /**
     * 商品id
     */
    private Long itemId;

    /**
     * skuid
     */
    private Long skuId;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 售价快照
     */
    private BigDecimal sellPriceSnapshot;

    /**
     * 商品名快照
     */
    private String itemNameSnapshot;

    /**
     * 商品规格
     */
    private String specificationSnapshot;

    /**
     * 购买数量
     */
    private Integer buyAmount;

    /**
     * 选中状态
     */
    private boolean selected;

    public void modifyBuyAmount(Integer buyAmount) {
        this.buyAmount = buyAmount;
    }

    public void select() {
        this.selected = true;
    }

    public void cancelSelect() {
        this.selected = false;
    }
}
