package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.pay.feign.PayFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/26/16:52
 * @Description:
 */
@Controller
@RequestMapping("/wxpay")
public class PayController {

    @Autowired
    private OrderFeign orderFeign;
    @Autowired
    private PayFeign payFeign;

    /**
     * 跳转到微信支付二维码页面
     * @param orderId 订单id
     * @param model 数据模型
     * @return String 返回的页面
     */
    @GetMapping
    public String wxPay(String orderId, Model model){
        //1.根据订单id查询对应的信息
        Result<Order> result = orderFeign.findById(orderId);
        if(result==null || result.getData()==null){
            return "fail";
        }
        Order order = result.getData();
        //2.根据订单的支付状态进行判断,如果不是未支付的订单,跳转到错误页面
        if(!"0".equals(order.getPayStatus())){
            return "fail";
        }
        //3.调用统一支付接口
        Result payResult = payFeign.nativePay(orderId, order.getPayMoney());
        if (payResult.getData() == null){
            return "fail";
        }

        //4.封装结果数据
        Map payMap = (Map) payResult.getData();
        payMap.put("orderId",orderId);
        payMap.put("payMoney",order.getPayMoney());

        model.addAllAttributes(payMap);
        return "wxpay";
    }

    //支付成功页面的跳转
    @RequestMapping("/toPaySuccess")
    public String toPaySuccess(Integer payMoney, Model model){
        model.addAttribute("payMoney",payMoney);
        return "paysuccess";
    }
}
