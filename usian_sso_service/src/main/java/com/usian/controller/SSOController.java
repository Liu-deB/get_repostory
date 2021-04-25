package com.usian.controller;

import com.usian.pojo.TbUser;
import com.usian.service.SSOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户注册与登录
 */
@RestController
@RequestMapping("service/sso")
public class SSOController {
    @Autowired
    private SSOServiceImpl ssoService;

    @RequestMapping("checkUserInfo/{checkValue}/{checkFlag}")
    public boolean checkUserInfo(@PathVariable("checkValue") String checkValue, @PathVariable("checkFlag") Integer checkFlag) {
        return ssoService.checkUserInfo(checkValue, checkFlag);
    }

    @RequestMapping("userRegister")
    public Integer userRegister(@RequestBody TbUser user) {
        return ssoService.userRegister(user);
    }

    @RequestMapping("userLogin")
    public Map userLogin(@RequestBody TbUser tbUser){
        return ssoService.userLogin(tbUser);
    }


}