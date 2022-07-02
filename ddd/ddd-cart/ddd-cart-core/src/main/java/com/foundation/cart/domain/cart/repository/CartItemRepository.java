package com.foundation.cart.domain.cart.repository;

import com.foundation.cart.domain.cart.entity.CartItem;

/**
 * @author : jacksonz
 * @date : 2022/5/29 11:42
 * @description :
 */
public interface CartItemRepository {

    public void saveCartItem(CartItem cartItem);
}
