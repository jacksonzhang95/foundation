package com.foundation.cart.domain.cart.repository;

import com.foundation.cart.domain.cart.valueobj.ItemInfo;
import com.foundation.cart.domain.cart.valueobj.ItemStockInfo;

/**
 * @author : jacksonz
 * @date : 2022/5/29 16:33
 * @description :
 */
public interface ItemStockRepository {


    ItemStockInfo getItemStockInfo(Param param);


    class Param {

    }
}
