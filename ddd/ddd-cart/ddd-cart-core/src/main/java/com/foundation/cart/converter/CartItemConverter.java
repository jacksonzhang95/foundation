package com.foundation.cart.converter;

import com.foundation.cart.domain.cart.entity.CartItem;
import com.foundation.cart.domain.cart.entity.CartItemID;
import com.foundation.cart.domain.cart.valueobj.ItemInfo;
import com.foundation.cart.domain.cart.valueobj.UserInfo;

/**
 * @author : jacksonz
 * @date : 2022/5/29 11:15
 * @description :
 */
public interface CartItemConverter {

    CartItemConverter INSTANCE = (CartItemConverter)new Object();

    default CartItem convert(ItemInfo itemInfo, Long userId) {

        Long itemId = itemInfo.getItemId();
        Long skuId = itemInfo.getSkuId();
        Long supplierId = itemInfo.getSupplierId();

        CartItem cartItem = new CartItem();
        cartItem.setId(new CartItemID(userId, itemId, skuId, supplierId));

        return cartItem;
    }


}
