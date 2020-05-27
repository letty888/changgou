package com.changgou.order.listener;

import com.changgou.entity.ResultCodeEnum;
import com.changgou.exception.CommonException;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/27/17:06
 * @Description:
 */
@Component
@Slf4j
public class OrderTaskListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_TACK)
    public void receiveMessage(String message) {
        if (StringUtils.isEmpty(message)) {
            log.error("{} 监听器中收到的消息为空,消息可能发生丢失.", "OrderTaskListener");
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR);
        }
        log.info("{}监听器中收到的消息是: {}", "OrderTaskListener", message);
        orderService.autoTack();
    }
}
