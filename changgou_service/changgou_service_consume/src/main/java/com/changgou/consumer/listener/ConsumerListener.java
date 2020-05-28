package com.changgou.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.consumer.config.RabbitMQConfig;
import com.changgou.consumer.service.SecKillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ConsumerListener {

    @Autowired
    private SecKillOrderService secKillOrderService;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
    public void receiveSecKillOrderMessage(Message message, Channel channel) {

        //消费者的预抓取总数设置(即削峰填谷,官方建议消费者的预抓取总数为100-300)
        try {
            channel.basicQos(300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //將消息轉換為 seckillOrder 对象
        SeckillOrder seckillOrder = JSON.parseObject(message.getBody(), SeckillOrder.class);
        if (seckillOrder == null) {
            log.error("{}监听器中收到的消息为空,消息可能丢失", "ConsumerListener");
        }
        log.info("{}监听器中接收到的消息是:" + "ConsumerListener", seckillOrder);
        //调用service层完成业务逻辑
        int result = secKillOrderService.createOrder(seckillOrder);
        if (result > 0) {
            //消息消费成功
            try {
                /**
                 * 第一个参数:消息的唯一标识
                 * 第二个参数:是否开启批处理
                 */
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            /**
             * 第一个参数:消息的唯一标识
             * 第二个参数: true所有消费者都会拒绝这个消息,false只有当前消费者拒绝
             * 第三个参数:true当前消息会进入到死信队列(延迟消息队列),false当前的消息会重新进入到原有队列中,默认回到头部
             */
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
