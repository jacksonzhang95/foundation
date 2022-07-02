package com.foundation.spring.web;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : jacksonz
 * @date : 2022/5/19 9:31
 */
public class Test {
}

class CartDomainService {


}

// 聚合根
class Cart {

    // 唯一ID标识 == userId
    Long id;

    // 购物车属性 - 总价
    BigDecimal totalSellingPrice;

    // 购物车属性 - 总折扣价
    BigDecimal totalDiscountPrice;

    // 选中数量
    Integer selectedCount;

    // 总数量
    Integer itemCount;

    // 实体
    List<CartInnerItem> cartInnerItems;

    // 商品加入购物车
    public void add(ItemInfo itemInfo) {
    }

    // 移除购物车商品
    public void remove(ItemInfo itemInfo) {
    }

    // 修改购物车商品购买数
    public void modifyBuyAmount(Long cartInnerItemId, Integer amount) {
    }

    // 选中购物车商品
    public void pickUp(Long cartInnerItemId) {
        // 维护聚合根属性
        this.selectedCount++;
        // 修改购物车商品状态
        // CartInnerItem#changeToSelected
    }

    // 取消选中购物车商品
    public void cancelPickUp(Long cartInnerItemId) {
        // 维护聚合根属性
        this.selectedCount--;
        // 修改购物车商品状态
        // CartInnerItem#changeToUnSelected
    }

    // 分享购物车
    public void shareToOther(List<Long> cartInnerItemId) {

    }

    // 复制其他人的购物车
    public void copyOtherCart(List<CartInnerItem> cartInnerItems) {

    }

}

class CartInnerItem  {

    private long id;

    private Integer type;

    private Integer buyAmount;

    private BigDecimal sellingPrice;

    private ItemSnapshot itemSnapshot;

    private List<Tip> tipList;

    private List<Label> label;

}

class ItemSnapshot {

    private Long itemId;

    private Long skuId;

    private Long supplierId;

    private String itemName;

    private String itemUrl;


}

class Tip {}
class Label {}
class ItemInfo{}