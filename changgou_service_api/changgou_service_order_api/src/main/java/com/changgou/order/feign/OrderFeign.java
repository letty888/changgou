package com.changgou.order.feign;

import com.changgou.entity.Result;
import com.changgou.order.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/25/21:05
 * @Description:
 */
@FeignClient(name = "order")
public interface OrderFeign {

    /***
     * 新增数据
     * @param order 订单
     * @return Result
     */
    @PostMapping("/order")
    Result add(@RequestBody Order order);

    /***
     * 根据ID查询数据
     * @param id 订单id
     * @return Result
     */
    @GetMapping("/order/{id}")
    Result<Order> findById(@PathVariable String id);
}
