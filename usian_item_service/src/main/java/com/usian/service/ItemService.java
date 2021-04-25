package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.config.RedisClient;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;
    
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    
    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisClient redisClient;

    @Value("{BASE}")
    private String BASE;

    @Value("{ITEM_INFO}")
    private String ITEM_INFO;

    public TbItem getById(Long itemId) {
        String key = ITEM_INFO + ":" + itemId + ":" + BASE;
        TbItem tbItem = (TbItem)redisClient.get(key);
        if (tbItem != null){
            return tbItem;
        }
        tbItem= tbItemMapper.selectByPrimaryKey(itemId);
        if (tbItem != null){
            redisClient.set(key,tbItem);
            return tbItem;
        }
        redisClient.set(key,tbItem);
        redisClient.expire(key,60);
        return tbItem;
    }

    public PageResult selectTbItemAllByPage(Integer page,Integer rows){
        PageHelper.startPage(page,rows);
        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.setOrderByClause("updated DESC");
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo((byte)1);
        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        for (int i = 0; i < tbItemList.size(); i++) {
            TbItem tbItem = tbItemList.get(i);
            tbItem.setPrice(tbItem.getPrice()/100);
        }
        PageInfo<TbItem> tbItemPageInfo = new PageInfo<>(tbItemList);
        PageResult pageResult = new PageResult();
        pageResult.setResult(tbItemPageInfo.getList());
        pageResult.setTotalPage(Long.valueOf(tbItemPageInfo.getPages()));
        pageResult.setPageIndex(tbItemPageInfo.getPageNum());
        return pageResult;
    }

    public Map<String, Object> preUpdateItem(Long itemId) {
        Map<String, Object> map = new HashMap<>();
        //根据商品 ID 查询商品
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item", item);
        //根据商品 ID 查询商品描述
        TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc", itemDesc.getItemDesc());
        //根据商品 ID 查询商品类目
        TbItemCat itemCat = tbItemCatMapper.selectByPrimaryKey(item.getCid());
        map.put("itemCat", itemCat.getName());
        //根据商品 ID 查询商品规格信息
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> list = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if (list != null && list.size() > 0) {
            map.put("itemParamItem", list.get(0).getParamData());
        }
        return map;
    }

    public Integer insertTbItem(TbItem tbItem,String desc,String itemParams){
        //补齐 Tbitem 数据
        Long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setUpdated(date);
        tbItem.setCreated(date);
        tbItem.setPrice(tbItem.getPrice()*100);
        Integer tbItemNum = tbItemMapper.insertSelective(tbItem);

        //补齐商品描述对象
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        Integer tbitemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);

        //补齐商品规格参数
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setCreated(date);
        Integer itemParamItmeNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);

        amqpTemplate.convertAndSend("item_exchage","item.add", itemId);
        return tbItemNum + tbitemDescNum + itemParamItmeNum;
    }


    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {
        Date date = new Date();
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        tbItem.setStatus((byte)1);
        int itemInteger = tbItemMapper.updateByPrimaryKey(tbItem);

        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(tbItem.getId());
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(desc);
        int tbItemDescInteger = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);

        TbItemParamItem tbItemParam = new TbItemParamItem();
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(tbItem.getId());
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExample(example);
        if (tbItemParamItems != null && tbItemParamItems.size() > 0){
            tbItemParam.setId(tbItemParamItems.get(0).getId());
            tbItemParam.setCreated(date);
            tbItemParam.setUpdated(date);
            tbItemParam.setParamData(itemParams);
        }
        int i = tbItemParamItemMapper.updateByPrimaryKeySelective(tbItemParam);
        return itemInteger + tbItemDescInteger + i;
    }

    public Integer deleteItemById(Long itemId) {
        TbItem tbItem = new TbItem();
        tbItem.setStatus((byte)0);
        tbItem.setId(itemId);
        int integer = tbItemMapper.updateByPrimaryKeySelective(tbItem);
        return integer;
    }

    public TbItemDesc selectItemDescByItemId(Long itemId) {
        return tbItemDescMapper.selectByPrimaryKey(itemId);
    }

    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if (tbItemParamItems != null && tbItemParamItems.size() >0){
            return tbItemParamItems.get(0);
        }
        return null;
    }
}
