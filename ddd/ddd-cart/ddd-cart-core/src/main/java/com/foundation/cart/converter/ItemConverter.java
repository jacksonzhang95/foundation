package com.foundation.cart.converter;

import com.foundation.cart.domain.cart.repository.ItemRepository;
import com.foundation.cart.domain.cart.repository.ItemStockRepository;
import com.foundation.cart.domain.cart.service.UserCartDomainService;
import com.foundation.cart.domain.cart.valueobj.ItemInfo;

/**
 * @author : jacksonz
 * @date : 2022/5/29 15:14
 * @description :
 */
public interface ItemConverter {

    ItemConverter INSTANCE = null;

    ItemRepository.Param converteToSearchItemInfoParam(UserCartDomainService.UserCardAddItemCommand.ItemInfo source);

    ItemStockRepository.Param converteToSearchItemStockInfoParam(ItemInfo source);

}
