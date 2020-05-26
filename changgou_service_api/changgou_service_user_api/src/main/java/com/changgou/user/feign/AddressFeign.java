package com.changgou.user.feign;

import com.changgou.entity.Result;
import com.changgou.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/25/16:51
 * @Description:
 */
@FeignClient(name = "user")
public interface AddressFeign {

    /**
     * 根据用户名查询对应的收获地址信息
     * @return Result<List<Address>>
     */
    @GetMapping("/address/list")
    public Result<List<Address>> listByUsername();
}
