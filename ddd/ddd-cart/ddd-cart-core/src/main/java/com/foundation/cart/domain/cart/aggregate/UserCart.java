package com.foundation.cart.domain.cart.aggregate;

import com.foundation.cart.converter.CartItemConverter;
import com.foundation.cart.domain.cart.entity.CartItem;
import com.foundation.cart.domain.cart.entity.CartItemID;
import com.foundation.cart.domain.cart.entity.UserCartID;
import com.foundation.cart.domain.cart.repository.ItemRepository;
import com.foundation.cart.domain.cart.valueobj.ItemInfo;
import com.foundation.cart.domain.cart.valueobj.TopicLabelInfo;
import com.foundation.cart.exception.CartItemNotExistException;
import com.foundation.cart.infrastructure.config.CartConfig;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : jacksonz
 * @date : 2022/5/28 15:59
 * @description 用户购物车聚合根
 */
public class UserCart {

    /**
     * 唯一标识
     */
    private UserCartID id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 购物车标签
     */
    private List<TopicLabelInfo> labelInfos;

    /**
     * 购物车商品信息
     */
    private List<CartItem> cartItems;

    /*
    好处：
        1. 聚合根的逻辑封装性更强
    坏处：
        1. 构建聚合根的时候需要依赖DI框架，聚合根入侵了技术层

    private transient CartItemRepository cartItemRepository;

    public void addItemV1(ItemInfo itemInfo) {
        CartItem cartItem = CartItemConverter.INSTANCE.convert(itemInfo, this.userId);
        cartItemRepository.saveCartItem(cartItem);
    }
     */

    public CartItem addItem(ItemInfo itemInfo) {
        CartItem cartItem = CartItemConverter.INSTANCE.convert(itemInfo, this.userId);
        this.cartItems.add(cartItem);
        return cartItem;
    }

    public CartItem removeCartItem(CartItemID cartItemID) {
        CartItem cartItem = searchCartItem(cartItemID);
        this.cartItems.remove(cartItem);
        return cartItem;
    }

    public CartItem modifyCartItemBuyAmount(CartItemID cartItemID, Integer buyAmount) {
        CartItem cartItem = searchCartItem(cartItemID);
        cartItem.modifyBuyAmount(buyAmount);
        return cartItem;
    }

    public CartItem selectCartItem(CartItemID cartItemID) {
        CartItem cartItem = searchCartItem(cartItemID);
        cartItem.select();
        return cartItem;
    }

    public CartItem cancelSelectCartItem(CartItemID cartItemID) {
        CartItem cartItem = searchCartItem(cartItemID);
        cartItem.cancelSelect();
        return cartItem;
    }

    public Integer countCartItem() {
        // TODO;
        return 0;
    }

    public Integer countCartSku() {
        // TODO
        return 0;
    }

    public boolean isFull(ItemInfo itemInfo) {
        // TODO 唯一key的生成方法应该写在那里
        Set<String> itemSkuUniqueKeySet = this.cartItems.stream().map(e -> String.valueOf(ItemRepository.buildItemUniqueKey(e))).collect(Collectors.toSet());
        // 添加待加入的商品
        itemSkuUniqueKeySet.add(ItemRepository.buildItemUniqueKey(itemInfo));
        return itemSkuUniqueKeySet.size() > CartConfig.CART_SKU_AMOUNT_LIMIT;
    }

    private CartItem searchCartItem(CartItemID cartItemID) {
        Optional<CartItem> cartItemOptional = this.cartItems.stream().filter(cartItem -> Objects.equals(cartItem.getId(), cartItemID)).findFirst();
        if (!cartItemOptional.isPresent()) {
            throw new CartItemNotExistException();
        }
        return cartItemOptional.get();
    }

    /*
        业务功能
            1. 购物车添加商品
            2. 购物车移除商品
                批量，单一
            3. 购物车修改商品购买数
            4. 购物车选中商品
                批量，单一
            5. 购物车统计商品总数(总数)
            6. 购物车统计商品类型总数(sku维度)
            7. 购物车是否购满
            8. 购物车计算价格
            9. 复制购物车
            10. 分享购物车商品

        展示功能
            凑单维度

            商品维度
                1. 标签(库存紧张, [直降一百元]特价优惠?, 88VIP特价，有优惠券)
                2. 商城活动特性値(聚划算结束时间？，预售尾款支付时间？，预热商品开买时间？)

            商品组维度
                供应商

       购物车搜索功能
            凑单

     */
}
