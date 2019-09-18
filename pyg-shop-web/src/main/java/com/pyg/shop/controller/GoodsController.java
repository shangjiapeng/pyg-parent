package com.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.PageResult;
import com.pyg.entity.ResultInfo;
import com.pyg.pojo.TbGoods;
import com.pyg.pojogroup.Goods;
import com.pyg.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody Goods goods) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录的商家id
        goods.getGoods().setSellerId(sellerId);//给所添加的商品设置商家的id
        try {
            goodsService.add(goods);
            return new ResultInfo(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody Goods goods) {
        //获取当前商家的id
        String selllerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //判断商家修改的是否是自己的商品
        Goods goods2 = goodsService.findOne(goods.getGoods().getId());
        if (!goods2.getGoods().getSellerId().equals(selllerId) || !goods.getGoods().getSellerId().equals(selllerId)) {
            return new ResultInfo(false, "非法操作");
        }
        try {
            goodsService.update(goods);
            return new ResultInfo(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public ResultInfo delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        //查询当前商家的商品(因为goodsService会被运营商后台和商家都调用,二运营商的后台查询所有商家的数据,所以
        //条件需要在商家后台这里设置)
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }

    //修改商品的上下架状态
    @RequestMapping("/updateStatus")
    public ResultInfo updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return new ResultInfo(true, "修改状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改状态失败");
        }
    }

}
