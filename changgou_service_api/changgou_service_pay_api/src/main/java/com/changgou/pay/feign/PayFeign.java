package com.changgou.pay.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pay")
public interface PayFeign {

    @GetMapping("/wxpay/nativePay")
     Result nativePay(@RequestParam("orderId") String orderId, @RequestParam("money")Integer money);

    /**
     * 查询订单
     * @param orderId 订单id
     * @return Result
     */
    @GetMapping("/wxpay/query/{orderId}")
     Result queryOrder(@PathVariable("orderId") String orderId);

    /**
     * 基于微信关闭订单
     * @param orderId 订单id
     * @return Result
     */
    @PutMapping("/wxpay/close/{orderId}")
     Result closeOrder(@PathVariable("orderId") String orderId);
}
