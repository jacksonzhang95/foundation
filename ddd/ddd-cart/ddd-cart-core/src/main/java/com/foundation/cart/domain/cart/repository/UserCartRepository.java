package com.foundation.cart.domain.cart.repository;

import com.foundation.cart.domain.cart.aggregate.UserCart;

/**
 * @author : jacksonz
 * @date : 2022/5/29 14:57
 * @description : 购物车仓库
 */
public interface UserCartRepository {

    void save(UserCart userCart);

    UserCart get(Long userId);
}
