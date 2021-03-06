package com.usian.controller;

import com.usian.pojo.TbItemParam;
import com.usian.service.ItemParamServiceImpl;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("service/itemParam")
public class TbItemParamController {

    @Autowired
    private ItemParamServiceImpl itemParamService;

    @RequestMapping("selectItemParamByItemCatId")
    public TbItemParam selectItemParamByItemCatId(Long itemCatId){
        return itemParamService.selectItemParamByItemCatId(itemCatId);
    }

    @RequestMapping("selectItemParamAll")
    public PageResult selectItemParamAll(Integer page,Integer rows){
        return itemParamService.selectItemParamAll(page,rows);
    }

    @RequestMapping("insertItemParam")
    public Integer insertItemParam(Long itemCatId,String paramData){
        return itemParamService.insertItemParam(itemCatId,paramData);
    }

    @RequestMapping("deleteItemParamById")
    public Integer deleteItemParamById(Long id){
        return itemParamService.deleteItemParamById(id);
    }
}
