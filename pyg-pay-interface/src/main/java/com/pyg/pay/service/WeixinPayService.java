package com.pyg.pay.service;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WeixinPayService {

    /***
     * 生成二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额(分)
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 查询支付的状态
     * @param out_trade_no 订单号
     * @return
     */
    public Map queryPayStatus(String out_trade_no);

    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);

}
