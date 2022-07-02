package com.foundation.cart.domain.cart.valueobj;

import lombok.Data;

/**
 * @author : jacksonz
 * @date : 2022/5/29 16:34
 * @description :
 */
@Data
public class ItemStockInfo {

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
     * 仓库编码
     */
    private String stockCode;

    /**
     * 库存值
     */
    private Integer stockAmount;
}
