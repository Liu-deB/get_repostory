package com.usian.service;

import com.usian.config.RedisClient;
import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * 用户注册与登录业务层
 */
@Service
@Transactional
public class SSOServiceImpl implements Serializable {
    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${USER_INFO}")
    private String USER_INFO;


    /**
     * 对用户的注册信息(用户名与电话号码)做数据校验
     */
    public boolean checkUserInfo(String checkValue, Integer checkFlag) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        // 1、查询条件根据参数动态生成：1、2分别代表username、phone
        if (checkFlag == 1) {
            criteria.andUsernameEqualTo(checkValue);
        } else if (checkFlag == 2) {
            criteria.andPhoneEqualTo(checkValue);
        }else {
            return false;
        }
        // 2、从tb_user表中查询数据
        List<TbUser> list = tbUserMapper.selectByExample(example);
        // 3、判断查询结果，如果查询到数据返回false。
        if (list == null || list.size() == 0) {
            // 4、如果没有返回true。
            return true;
        }
        // 5、如果有返回false。
        return false;
    }

    public Integer userRegister(TbUser user) {
        //将密码做加密处理。
        String pwd = MD5Utils.digest(user.getPassword());
        user.setPassword(pwd);
        //补齐数据
        user.setCreated(new Date());
        user.setUpdated(new Date());
        return tbUserMapper.insert(user);
    }

    public Map<String,Object> userLogin(TbUser user) {
        // 1、判断用户名密码是否正确。
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        criteria.andPasswordEqualTo(MD5Utils.digest(user.getPassword()));
        List<TbUser> userList = tbUserMapper.selectByExample(example);
        if(userList == null || userList.size() <= 0){
            return null;
        }

        String token = UUID.randomUUID().toString();
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        map.put("userid",userList.get(0).getId());
        map.put("username",userList.get(0).getUsername());

        String key = USER_INFO + ":" + token;
        redisClient.set(key,userList.get(0));
        redisClient.expire(key,300);
        return map;
    }

}