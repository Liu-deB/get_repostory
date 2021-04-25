package com.usian.controller;

import com.usian.feign.SSOServiceFeign;
import com.usian.pojo.TbUser;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户注册与登录
 */
@RestController
@RequestMapping("frontend/sso")
public class SSOController {

    @Autowired
    private SSOServiceFeign ssoSeriviceFeign;

    @RequestMapping("checkUserInfo/{checkValue}/{checkFlag}")
    public Result checkUserInfo(@PathVariable("checkValue") String checkValue,
                                @PathVariable("checkFlag") Integer checkFlag){
        Boolean checkUserInfo= ssoSeriviceFeign.checkUserInfo(checkValue,checkFlag);
        if(checkUserInfo){
            return Result.ok(checkUserInfo);
        }
        return Result.error("校验失败");
    }

    /**
     * 用户注册
     */
    @RequestMapping("userRegister")
    public Result userRegister(TbUser user){
        Integer userRegister = ssoSeriviceFeign.userRegister(user);
        if(userRegister==1){
            return Result.ok();
        }
        return Result.error("注册失败");
    }

    @RequestMapping("userLogin")
    public Result userLogin(TbUser tbUser){
        Map map = ssoSeriviceFeign.userLogin(tbUser);
        if (map != null){
            return Result.ok(map);
        }
        return Result.error("登录失败");
    }
}