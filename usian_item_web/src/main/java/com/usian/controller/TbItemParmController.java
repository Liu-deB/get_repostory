package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("backend/itemParam")
public class TbItemParmController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @RequestMapping("selectItemParamByItemCatId/{itemCatId}")
    public Result selectItemParamByItemCatId(@PathVariable("itemCatId") Long itemCatId){
        TbItemParam tbItemParam = itemServiceFeign.selectItemParamByItemCatId(itemCatId);
        if (tbItemParam != null){
            return Result.ok(tbItemParam);
        }
        return Result.error("查无结果");
    }

    @RequestMapping("selectItemParamAll")
    public Result selectItemParamAll(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "5") Integer rows){
        PageResult pageResult = itemServiceFeign.selectItemParamAll(page, rows);
        if (pageResult.getResult().size() > 0){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }

    @RequestMapping("insertItemParam")
    public Result insertItemParam(Long itemCatId,String paramData){
        Integer integer = itemServiceFeign.insertItemParam(itemCatId, paramData);
        if (integer == 1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    @RequestMapping("deleteItemParamById")
    public Result deleteItemParamById(Long id){
        Integer integer = itemServiceFeign.deleteItemParamById(id);
        if (integer != null){
            return Result.ok(integer);
        }
        return Result.error("删除失败");
    }
}
