package com.changgou.seckill.service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/28/9:24
 * @Description:
 */
public interface SecKillOrderService {

    /**
     * 秒殺商品下單
     * @param username 用戶名
     * @param time 秒殺時間段
     * @param id 商品id
     * @return boolean
     */
    boolean add(String username, String time, Long id);
}
