package com.changgou.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.config.RabbitMQConfig;
import com.changgou.seckill.config.SendMessageConfirm;
import com.changgou.seckill.config.TokenDecode;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SecKillOrderService;
import com.changgou.utils.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/28/9:26
 * @Description:
 */
@Service
public class SecKillOrderServiceImpl implements SecKillOrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TokenDecode tokenDecode;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SendMessageConfirm sendMessageConfirm;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    public static final String SECKILL_GOODS_KEY = "seckill_goods_";
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY = "seckill_goods_stock_count_";

    /**
     * 秒殺商品下單
     *
     * @param username 用戶名
     * @param time     秒殺時間段
     * @param id       商品id
     * @return boolean
     */
    @Override
    public boolean add(String username, String time, Long id) {

        //防止用户恶意刷单
        String s = this.preventRepeatCommit(username, id);
        if ("fail".equals(s)) {
            return false;
        }

        //防止相同商品重复购买
        SeckillOrder order = seckillOrderMapper.getOrderInfoByUserNameAndGoodsId(username, id);
        if (order != null){
            return false;
        }

        //获取秒杀商品详情
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS_KEY + time).get(String.valueOf(id));
        //获取秒杀商品在redis中的库存
        String stockCount = String.valueOf(redisTemplate.boundValueOps(SECKILL_GOODS_STOCK_COUNT_KEY + id).get());
        //健壮性判断
        if (seckillGoods == null || StringUtils.isEmpty(stockCount) || Integer.parseInt(stockCount) <= 0) {
            return false;
        }
        //利用redis的原子性减少存库(返回值是减少后的值)
        Long decrement = redisTemplate.boundValueOps(SECKILL_GOODS_STOCK_COUNT_KEY + id).decrement();
        if (decrement <= 0) {
            //说明该商品已经没有库存,则从redis中删除掉该商品的信息
            redisTemplate.boundHashOps(SECKILL_GOODS_KEY + time).delete(String.valueOf(id));
            redisTemplate.delete(SECKILL_GOODS_STOCK_COUNT_KEY + id);
        }


        //发送消息(保证消息生产者对于消息的不丢失实现)
        //消息体: 秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");
        sendMessageConfirm.sendMessage("", RabbitMQConfig.SECKILL_ORDER_QUEUE, JSON.toJSONString(seckillOrder));
        // rabbitTemplate.convertAndSend("", RabbitMQConfig.SECKILL_ORDER_QUEUE, JSON.toJSONString(seckillOrder));
        return true;
    }

    /**
     * 基于redis防止用户恶意刷单
     *
     * @return String
     */
    private String preventRepeatCommit(String username, Long id) {
        String redis_key = "seckill_user_" + username + "_id_" + id;
        Long count = redisTemplate.boundValueOps(redis_key).increment();

        if (count == 1) {
            redisTemplate.expire(redis_key, 5, TimeUnit.MINUTES);
            return "success";
        }
        if (count > 1) {
            return "fail";
        }
        return "fail";
    }


}
