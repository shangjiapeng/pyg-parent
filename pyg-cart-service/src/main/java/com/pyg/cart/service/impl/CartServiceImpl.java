package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加商品到购物车
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1 根据商品的sku id 查商品的Sku信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品的状态无效");
        }
        //2 获取商家的id
        String sellerId = item.getSellerId();
        //3 根据商家的id 判断购物车列表中是否存在商品所属商家的购物车对象
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4 如果购物新列表中不存在该商家的购物车
        if (cart == null) {
            //4.1创建一个新的购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //4.2创建购物车明细表
            List orderItemList = new ArrayList();
            //4.3创建新的购物车明细对象,并添加到明细表中
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.4将新的购物车对象添加到购物车列表
            cartList.add(cart);
        } else {
            //5 如果购物车列表中存在该商家的购物车
            //判断购物车明细中是已经添加过该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (orderItem == null) {
                //5.1如果该商家购物车中没有添加该商品,在购物车中新增商品明细
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                //5.2如果有,则只用更改商品的数量,并更新金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));//商业计算公式
                //5.3如果数量操作后小于等于0,则移除该商品
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);//移除此商家购物车中该条商品的明细信息
                }
                //5.4如果移除后该商家cart的明细数量为0,则将商家的cart移除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }


    /**
     * 根据商家id 查询购物车对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据商品id查询购物车中是否已经存在该商品的明细信息
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 新建购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    /**
     * 从redis中查询购物车
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据..."+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            cartList=new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 将购物车保存到redis中去
     *
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        try {
            System.out.println("向redis中存入购物车的数据..."+username);
            redisTemplate.boundHashOps("cartList").put(username,cartList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("添加失败..");
        }

    }

    /**
     * 合并购物车
     *
     * @param cartListRedis
     * @param cartListCookies
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartListRedis, List<Cart> cartListCookies) {
        System.out.println("合并购物车...");
        for (Cart cart : cartListCookies) {//遍历新产生的购物车
            List<TbOrderItem> orderItemList = cart.getOrderItemList();//获取商品明细
            for (TbOrderItem orderItem : orderItemList) {//遍历商品明细集合
                //把集合中的单个的商品明细信息,合并到远程旧的购物车数据中
                cartListRedis=addGoodsToCartList(cartListRedis,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartListRedis;
    }
}
