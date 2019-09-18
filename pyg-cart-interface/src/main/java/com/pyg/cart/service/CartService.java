package com.pyg.cart.service;

import com.pyg.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart>addGoodsToCartList(List<Cart>cartList,Long itemId,Integer num);

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    public List<Cart>findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis中去
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartListRedis
     * @param cartListCookies
     * @return
     */
    public List<Cart> mergeCartList(List<Cart>cartListRedis,List<Cart>cartListCookies);

}
