package com.pyg.seckill.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import com.pyg.entity.PageResult;
import com.pyg.entity.ResultInfo;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillOrder> findAll() {
        return seckillOrderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return seckillOrderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.add(seckillOrder);
            return new ResultInfo(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.update(seckillOrder);
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
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderService.findOne(id);
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
            seckillOrderService.delete(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param seckillOrder
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSeckillOrder seckillOrder, int page, int rows) {
        return seckillOrderService.findPage(seckillOrder, page, rows);
    }

    /**
     * 提交订单
     * @param seckillId
     * @return
     */
    @RequestMapping("/submitOrder")
    public ResultInfo submitOrder(Long seckillId){
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userId)){//如果未登录
            return new ResultInfo(false,"用户未登录");
        }
        try {
            seckillOrderService.submitOrder(seckillId,userId);
            return new ResultInfo(true,"提交成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new ResultInfo(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "提交失败");
        }
    }
}
