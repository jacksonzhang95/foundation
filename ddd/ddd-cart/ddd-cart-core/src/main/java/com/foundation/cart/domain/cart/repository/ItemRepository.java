package com.foundation.cart.domain.cart.repository;

import com.foundation.cart.domain.cart.entity.CartItem;
import com.foundation.cart.domain.cart.valueobj.ItemInfo;
import lombok.Data;

import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/5/29 14:44
 * @description :
 */
public interface ItemRepository {

    String UNIQUE_KEY_PATTERN = "item:%s:sku:%s:supplier:%s";

    ItemInfo getItemInfo(Param param);

    static String buildItemUniqueKey(ItemInfo itemInfo) {
        if (Objects.isNull(itemInfo)) {
            // TODO
        }
        return buildUnique(itemInfo.getItemId(), itemInfo.getSkuId(), itemInfo.getSupplierId());
    }

    static String buildItemUniqueKey(CartItem cartItem) {
        if (Objects.isNull(cartItem)) {
            // TODO
        }
        return buildUnique(cartItem.getItemId(), cartItem.getSkuId(), cartItem.getSupplierId());

    }

    static String buildUnique(Long itemId, Long skuId, Long supplierId) {
        return String.format(UNIQUE_KEY_PATTERN, itemId, skuId, supplierId);
    }

    @Data
    class Param {

        private Long itemId;

        private Long skuId;

        private Long supplierId;

        private Integer stockCode;
    }
}
