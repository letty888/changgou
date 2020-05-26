package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.ResultCodeEnum;
import com.changgou.exception.CommonException;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.user.feign.AddressFeign;
import com.changgou.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/25/16:56
 * @Description: 订单结算渲染页面
 */
@Controller
@RequestMapping("/worder")
public class OrderController {

    @Autowired
    private AddressFeign addressFeign;
    @Autowired
    private CartFeign cartFeign;
    @Autowired
    private OrderFeign orderFeign;

    @GetMapping("/ready/order")
    public String readyOrder(Model model) {

        //获取收件地址信息
        List<Address> addressList = addressFeign.listByUsername().getData();

        //获取购物车中的信息
        Map map = cartFeign.list();
        if (addressList == null || addressList.size() <= 0 || map == null || map.size() <= 0) {
            throw new CommonException(ResultCodeEnum.NO_DATA);
        }

        List<OrderItem> orderItemList = (List<OrderItem>) map.get("orderItemList");
        Integer totalNum = (Integer) map.get("totalNum");
        Integer totalMoney = (Integer) map.get("totalMoney");

        model.addAttribute("address", addressList);
        model.addAttribute("carts",orderItemList);
        model.addAttribute("totalMoney",totalMoney);
        model.addAttribute("totalNum",totalNum);

        //默认收件人信息
        for (Address address : addressList) {
            if("1".equals(address.getIsDefault())){
                model.addAttribute("deAddr",address);
                break;
            }
        }

        return "order";
    }




    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody Order order){
        return orderFeign.add(order);
    }

    @GetMapping("/toPayPage")
    public String toPayPage(String orderId,Model model){
        Order order =orderFeign.findById(orderId).getData();
        model.addAttribute("orderId",orderId);
        model.addAttribute("payMoney",order.getPayMoney());
        return "pay";
    }
}
