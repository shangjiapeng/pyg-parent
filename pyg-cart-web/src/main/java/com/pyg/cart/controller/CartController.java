package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.cart.service.CartService;
import com.pyg.entity.ResultInfo;
import com.pyg.pojogroup.Cart;
import com.pyg.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 从cookie中提取购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //当前登录人的账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人:"+username);

        //读取本地cookie中的购物车数据
        System.out.println("从cookie中提取购物车");
        String cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartList==null||cartList.equals("")){
            System.out.println("cookie中数据为空...");
            cartList="[]";//定义内容为空的格式,避免报错
        }
        List<Cart>cartList_cookie= JSON.parseArray(cartList,Cart.class);

        if (username.equals("anonymousUser")){//如果未登录
            return cartList_cookie;
        }else {//如果已经登录,从redis中提取
            System.out.println("从redis中读取购物车...");
            List<Cart>cartList_redis=cartService.findCartListFromRedis(username);//从redis中提取
            if (cartList_cookie.size()>0){//如果本地存在购物车
                //合并购物车
                cartList_redis=cartService.mergeCartList(cartList_redis,cartList_cookie);
                //清楚本地的cookie的cartList数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //再将合并之后的数据存入redis
                cartService.saveCartListToRedis(username,cartList_redis);
                System.out.println("合并了购物车.....");
            }
            return cartList_redis;
        }
    }

    /**
     * 添加商品到购物车
     * @param itemId
     * @param num
     * @return
     */

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public ResultInfo addGoodsToCartList(Long itemId, Integer num){
        /*//设置可以访问的域(当此方法不需要操作cookie)
        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
        //如果操作的方法需要使用到cookie,必须加上这句话
        response.setHeader("Access-Control-Allow-Credentials", "true");*/

        //当前登录的人
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户"+username);

        try {//从Cookie中获取购物车列表
            List<Cart> cartList = findCartList();
            //调用服务方法更新购物车列表
            cartList= cartService.addGoodsToCartList(cartList,itemId,num);
            //如果是未登录,则保存到cookie
            if (username.equals("anonymousUser")) {
                String cartListString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request, response, "cartList", cartListString,
                        3600 * 24, "utf-8");
                System.out.println("向cookie存入数据...");
            }else {//如果已经登录了,保存到redis中
                cartService.saveCartListToRedis(username, cartList);
                System.out.println("向redis存入数据...");
            }
            return new ResultInfo(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false,"添加失败");
        }
    }

}
