package com.foundation.cart.domain.cart.valueobj;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/5/29 11:05
 * @description : 基础商品信息
 */
@Data
public class ItemInfo {

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
     * 售价
     */
    private BigDecimal sellPrice;

    /**
     * 商品名（短）
     */
    private String itemShortName;

    /**
     * 商品名
     */
    private String itemName;

    /**
     * 商品当前状态
     */
    private Integer status;

    /**
     * 商品规格
     */
    private List<SpecificationInfo> specifications;

    @Data
    public static class SpecificationInfo {

        /**
         * 规格id
         */
        private Long id;

        /**
         * 规格键
         */
        private String key;

        /**
         * 规格值
         */
        private String value;
    }

}
