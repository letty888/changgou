package com.changgou.seckill.config;

import com.changgou.utils.IdWorker;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/28/10:32
 * @Description:
 */
@Component
public class SendMessageConfirm implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    public SendMessageConfirm(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this);
    }

    public static final String MESSAGE_CONFIRM_KEY = "message_confirm_";

    /**
     * 消息服务器持久化消息后返回的通知(此方法相当于一个监听方法,即消息服务器持久化完成后,是否成功都会返回一个通知,这个方法就是接收通知的方法)
     *
     * @param correlationData 消息载体信息
     * @param ack             是否持久化成功
     * @param cause           失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            //说明消息已经成功持久化到消息服务器的磁盘上,则删除掉redis中的数据
            redisTemplate.delete(correlationData.getId());
            redisTemplate.delete(MESSAGE_CONFIRM_KEY + correlationData.getId());
        } else {
            //说明消息服务器并没有将消息成功持久化到服务器的磁盘上,则从redis中取出,并且重新发送消息
            Map<String, String> entries = redisTemplate.boundHashOps(MESSAGE_CONFIRM_KEY + correlationData.getId()).entries();
            String exchange = entries.get("exchange");
            String routingKey = entries.get("routingKey");
            String message = entries.get("message");
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        }
    }


    //自定义消息发送方法
    public void sendMessage(String exchange, String routingKey, String message) {
        CorrelationData correlationData = new CorrelationData(idWorker.nextId() + "");
        redisTemplate.boundValueOps(correlationData.getId()).set(message);

        Map<String, String> map = new HashMap<>(0);
        map.put("exchange", exchange);
        map.put("routingKey", routingKey);
        map.put("message", message);
        redisTemplate.boundHashOps(MESSAGE_CONFIRM_KEY + correlationData.getId()).putAll(map);

        //携带着本次消息的唯一标识,进行数据发送
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }
}
