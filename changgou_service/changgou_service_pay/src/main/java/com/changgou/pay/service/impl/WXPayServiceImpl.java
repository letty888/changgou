package com.changgou.pay.service.impl;

import com.changgou.pay.service.WXPayService;
import com.github.wxpay.sdk.WXPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class WXPayServiceImpl implements WXPayService {

    @Autowired
    private WXPay wxPay;

    @Value("${wxpay.notify_url}")
    private String notify_url;

    //统一下单接口调用
    @Override
    public Map nativePay(String orderId, Integer money) {
        try {
            //1. 封装请求参数
            Map<String, String> map = new HashMap<>();
            map.put("body", "畅购");
            map.put("out_trade_no", orderId);

            //这里将金额写死了,写成了1分
            //注意:前端是以元为单位,微信支付是以分为单位
            BigDecimal payMoney = new BigDecimal("0.01");
            BigDecimal fen = payMoney.multiply(new BigDecimal("100")); //1.00
            fen = fen.setScale(0, BigDecimal.ROUND_UP); // 1
            map.put("total_fee", String.valueOf(fen));

            map.put("spbill_create_ip", "127.0.0.1");
            //支付成功的回调地址
            map.put("notify_url", notify_url);
            map.put("trade_type", "NATIVE");

            return wxPay.unifiedOrder(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Map queryOrder(String orderId) {
        try {
            Map<String ,String> map = new HashMap(0);
            map.put("out_trade_no",orderId);
            Map<String, String> resultMap = wxPay.orderQuery(map);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
