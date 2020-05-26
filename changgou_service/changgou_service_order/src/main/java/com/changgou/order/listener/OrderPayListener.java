package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.ResultCodeEnum;
import com.changgou.exception.CommonException;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/26/22:23
 * @Description:
 */
@Component
@Slf4j
public class OrderPayListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAY)
    public void receiveMessage(String message) {
        log.info("接收到订单支付的消息是:" + message);
        if (StringUtils.isEmpty(message)) {
            log.error("接收到的消息为空,请注意消息可能丢失...");
            throw new RuntimeException();
        }
        Map<String,String> map = JSON.parseObject(message, Map.class);
        orderService.updateOrderStatus(map.get("orderId"),map.get("transactionId"));
    }
}
