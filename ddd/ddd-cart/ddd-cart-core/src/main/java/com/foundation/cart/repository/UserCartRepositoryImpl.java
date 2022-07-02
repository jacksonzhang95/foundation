package com.foundation.cart.repository;

import com.foundation.cart.domain.cart.aggregate.UserCart;
import com.foundation.cart.domain.cart.repository.UserCartRepository;

/**
 * @author : jacksonz
 * @date : 2022/5/29 16:46
 * @description :
 */
public class UserCartRepositoryImpl implements UserCartRepository {

    @Override
    public void save(UserCart userCart) {
        // 保存商品
    }

    @Override
    public UserCart get(Long userId) {
        return null;
    }
}
