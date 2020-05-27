package com.changgou.order.listener;

import com.changgou.entity.Result;
import com.changgou.entity.ResultCodeEnum;
import com.changgou.exception.CommonException;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.service.OrderService;
import com.changgou.pay.feign.PayFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/27/11:17
 * @Description:
 */
@Component
@Slf4j
public class OrderTimeOutListner {


    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "queue.ordertimeout")
    public void receiveMessgae(String message) {
        if (StringUtils.isEmpty(message)) {
            log.error("{} 监听器中接收到的消息为空,消息可能发生丢失现象", "OrderTimeOutListner");
        }
        log.info("{} 监听器中接收到的消息是: {}", "OrderTimeOutListner", message);

        try {
            //调用业务层关闭订单
            orderService.closeOrder(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
