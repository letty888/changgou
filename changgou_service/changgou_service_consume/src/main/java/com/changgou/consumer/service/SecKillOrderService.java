package com.changgou.consumer.service;

import com.changgou.seckill.pojo.SeckillOrder;

public interface SecKillOrderService {

    /**
     * 同步mysql中的数据
     * @param seckillOrder 秒杀订单
     * @return int
     */
    int createOrder(SeckillOrder seckillOrder);
}
