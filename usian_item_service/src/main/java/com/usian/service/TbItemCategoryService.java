package com.usian.service;

import com.usian.config.RedisClient;
import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class TbItemCategoryService implements Serializable {

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Value("${PROTAL_CATRESULT_KEY}")
    private String portal_catresult_redis_key;

    @Autowired
    private RedisClient redisClient;


    public List<TbItemCat> selectItemCategoryByParentId(Long id){
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id);
        criteria.andStatusEqualTo(1);
        List<TbItemCat> list = tbItemCatMapper.selectByExample(example);
        return list;
    }


    public CatResult selectItemCategoryAll(){
        CatResult redisCatResult1 = (CatResult)redisClient.get(portal_catresult_redis_key);
        if (redisCatResult1 != null){
            System.out.println("这是redis中的");
            return redisCatResult1;
        }
        System.out.println("这是数据库中的");
        CatResult catResult = new CatResult();
        catResult.setData(getCatList(0L));
        redisClient.set(portal_catresult_redis_key,catResult);
        return catResult;
    }

    private List<?> getCatList(Long parentId){
        //创建查询条件
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> list = tbItemCatMapper.selectByExample(example);
        List resultList = new ArrayList();
        int count = 0;
        for(TbItemCat tbItemCat:list){
            //判断是否是父节点
            if(tbItemCat.getIsParent()){
                CatNode catNode = new CatNode();
                catNode.setName(tbItemCat.getName());
                catNode.setItem(getCatList(tbItemCat.getId()));//查询子节点
                resultList.add(catNode);
                count++;
                //只取商品分类中的 18 条数据
                if (count == 18){
                    break;
                }
            }else{
                resultList.add(tbItemCat.getName());
            }
        }
        return resultList;
    }
}
