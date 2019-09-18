package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 生成二维码
     *
     * @param out_trade_no 订单号
     * @param total_fee    金额(分)
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1 创建参数
        Map<String, String> param = new HashMap<>();//创建参数
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", out_trade_no);//商户订单号
        param.put("total_fee", total_fee);//总金额(分)
        param.put("spbill_create_ip", "127.0.0.1");//终端IP
        param.put("notify_url", "http://test.itcast.cn");//通知地址
        param.put("trade_type", "NATIVE");//交易类型

        try {
            //2 生成要发送的xml,发送请求
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求的参数:"+xmlParam);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3.获取二维码生成结果
            String xmlResult = client.getContent();
            System.out.println("二维码生成的结果xml:"+xmlResult);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            Map<String, String> map = new HashMap<>();
            //只需要返回一部分的数据
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no",out_trade_no);//订单号
            System.out.println("返回的数据:"+map);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();//如果没有成功返回一个空的map
        }
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {

        Map param = new HashMap();
        //1设置参数
        param.put("appid",appid);//公总账号
        param.put("mch_id",partner);//商户号
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            //2发送请求
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3获取结果
            String result= client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println("支付的结果:"+map);
            return map;
        } catch (Exception e) {//如果出现错误则返回为空
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭支付
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid",appid);//公众号id
        param.put("mch_id",partner);//商户号
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        String url ="https://api.mch.weixin.qq.com/pay/closeorder";

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String resultXml = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(resultXml);
            System.out.println("微信支付关闭结果:..."+map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
