package com.usian.feign;

import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-sso-service")
public interface SSOServiceFeign {

    @RequestMapping("service/sso/checkUserInfo/{checkValue}/{checkFlag}")
    public boolean checkUserInfo(@PathVariable("checkValue") String checkValue,
                                 @PathVariable("checkFlag") Integer checkFlag);

    @RequestMapping("service/sso/userRegister")
    public Integer userRegister(@RequestBody TbUser user);

    @RequestMapping("service/sso/userLogin")
    Map userLogin(@RequestBody TbUser tbUser);
}
