package com.foundation.cart.domain.cart.service;

import com.foundation.cart.converter.ItemConverter;
import com.foundation.cart.domain.cart.aggregate.UserCart;
import com.foundation.cart.domain.cart.repository.ItemRepository;
import com.foundation.cart.domain.cart.repository.ItemStockRepository;
import com.foundation.cart.domain.cart.repository.UserCartRepository;
import com.foundation.cart.domain.cart.valueobj.ItemInfo;
import com.foundation.cart.domain.cart.valueobj.ItemStockInfo;
import com.foundation.cart.exception.CartFullException;
import com.foundation.cart.exception.ItemStockNotEnoughException;
import lombok.Data;

/**
 *
 * @author : jacksonz
 * @date : 2022/5/29 14:41
 * @description :
 */
public class UserCartDomainService {

    private UserCartRepository userCartRepository;

    private ItemRepository itemRepository;

    private ItemStockRepository itemStockRepository;

    public void addItem(UserCardAddItemCommand command) {
        // 构建聚合根
        UserCart userCart = userCartRepository.get(command.getUserId());
        // 查询商品域数据
        ItemInfo itemInfo = itemRepository.getItemInfo(ItemConverter.INSTANCE.converteToSearchItemInfoParam(command.getItemInfo()));
        // 查询库存域数据
        ItemStockInfo itemStockInfo = itemStockRepository.getItemStockInfo(ItemConverter.INSTANCE.converteToSearchItemStockInfoParam(itemInfo));
        // 检查商品状态
        if (checkItemStatus(itemInfo)) {
            // TODO
        }
        // 检查库存
        if (checkItemStock(itemStockInfo, command.getBuyAmount())) {
            throw new ItemStockNotEnoughException();
        }
        // 检查购物车是否加满
        if (userCart.isFull(itemInfo)) {
            throw new CartFullException();
        }
        // 添加商品
        userCart.addItem(itemInfo);
        // 保存聚合
        userCartRepository.save(userCart);
    }

    private boolean checkItemStatus(ItemInfo itemInfo) {
        // 判断商品是否在上架态
        return itemInfo.getStatus() == 1;
    }

    private boolean checkItemStock(ItemStockInfo itemStockInfo, Integer buyAmount) {
        return itemStockInfo.getStockAmount() >= buyAmount;
    }

    @Data
    public static class UserCardAddItemCommand {

        private Long userId;

        private Integer buyAmount;

        private ItemInfo itemInfo;

        @Data
        public static class ItemInfo {
            private Long itemId;

            private Long skuId;

            private Long supplierId;

            private Integer stockCode;
        }
    }

}
