package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.config.RedisClient;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentExample;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements Serializable {

    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;

    @Value("${AD_HEIGHT}")
    private Integer AD_HEIGHT;

    @Value("${AD_WIDTH}")
    private Integer AD_WIDTH;

    @Value("${AD_HEIGHTB}")
    private Integer AD_HEIGHTB;

    @Value("${AD_WIDTHB}")
    private Integer AD_WIDTHB;

    @Value("${PORTAL_AD_KEY}")
    private String portal_ad_redis_key;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TbContentMapper tbContentMapper;

    public PageResult selectTbContentAllByCategoryId(Integer page, Integer rows,Long categoryId) {
        PageHelper.startPage(page, rows);
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
        PageInfo<TbContent> pageInfo = new PageInfo<TbContent>(list);
        PageResult result = new PageResult();
        result.setPageIndex(pageInfo.getPageNum());
        result.setTotalPage(pageInfo.getTotal());
        result.setResult(pageInfo.getList());
        return result;
    }

    public Integer insertTbContent(TbContent tbContent){
        Date date = new Date();
        tbContent.setUpdated(date);
        tbContent.setCreated(date);
        Integer num = tbContentMapper.insertSelective(tbContent);
        redisClient.hdel(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        return num;
    }

    public Integer deleteContentByIds(Long id){
        redisClient.hdel(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        return tbContentMapper.deleteByPrimaryKey(id);
    }

    public List<AdNode> selectFrontendContentByAD() {
        //查询缓存
        List<AdNode> adNodeListRedis = (List<AdNode>)redisClient.hget(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        if(adNodeListRedis!=null){
            System.out.println("这是redis中");
            return adNodeListRedis;
        }
        System.out.println("这是数据库中的");
        // 查询TbContent
        TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(AD_CATEGORY_ID);
        List<TbContent> tbContentList = tbContentMapper.selectByExample(tbContentExample);
        List<AdNode> adNodeList = new ArrayList<AdNode>();
        for (TbContent tbContent : tbContentList) {
            AdNode adNode = new AdNode();
            adNode.setSrc(tbContent.getPic());
            adNode.setSrcB(tbContent.getPic2());
            adNode.setHref(tbContent.getUrl());
            adNode.setHeight(AD_HEIGHT);
            adNode.setWidth(AD_WIDTH);
            adNode.setHeightB(AD_HEIGHTB);
            adNode.setWidthB(AD_WIDTHB);
            adNodeList.add(adNode);
        }

        redisClient.hset(portal_ad_redis_key,AD_CATEGORY_ID.toString(),adNodeList);
        return adNodeList;
    }
}
