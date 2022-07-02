package com.foundation.cart.domain.cart.entity;

import lombok.Data;

/**
 * @author : jacksonz
 * @date : 2022/5/29 11:03
 * @description :
 */
@Data
public class CartItemID {

    /**
     *
     */
    private Long userId;

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

    public CartItemID() {
    }

    public CartItemID(Long userId, Long itemId, Long skuId, Long supplierId) {
        this.userId = userId;
        this.itemId = itemId;
        this.skuId = skuId;
        this.supplierId = supplierId;
    }
}
