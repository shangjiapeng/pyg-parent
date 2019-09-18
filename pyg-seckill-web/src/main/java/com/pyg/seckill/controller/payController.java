package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.ResultInfo;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制层
 */

@RestController
@RequestMapping("/pay")
public class payController {
    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前的用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //到redis中查询秒杀的订单
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        //判断秒杀的订单是否存在
        if (seckillOrder != null) {
            long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);//金额:分
            return weixinPayService.createNative(seckillOrder.getId() + "", fen + "");
        } else {
            return new HashMap();
        }

    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public ResultInfo queryPayStatus(String out_trade_no) {
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResultInfo result = null;
        int x = 0;
        while (true) {
            //调用查询接口
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {//出错
                result = new ResultInfo(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new ResultInfo(true, "支付成功");
                //保存订单
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            //不让循环无休止地运行定义变量，如果超过了这个值则退出循环，设置时间为 5 分钟
            if (x > 100) {
                result = new ResultInfo(false, "二维码超时");
                //调用微信的关闭订单接口,
                Map<String, String> payresult = weixinPayService.closePay(out_trade_no);
                if (payresult != null && "FAIL".equals(payresult.get("return_code"))) {
                    if ("ORDERPAID".equals(payresult.get("err_code"))) {
                        result = new ResultInfo(true, "已经支付成功");
                        //保存订单
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                    }
                }
                if (result.isSuccess() == false) {
                    System.out.println("超时，取消订单");
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
                break;
            }
        }
        return result;
    }
}
