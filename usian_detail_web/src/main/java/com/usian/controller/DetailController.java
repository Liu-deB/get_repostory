package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("frontend/detail")
public class DetailController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @RequestMapping("selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if (tbItem != null){
            return Result.ok(tbItem);
        }
        return Result.error("查询失败");
    }

    @RequestMapping("selectItemDescByItemId")
    public Result selectItemDescByItemId(Long itemId){
        TbItemDesc tbItemDesc = itemServiceFeign.selectItemDescByItemId(itemId);
        if (tbItemDesc != null){
            return Result.ok(tbItemDesc);
        }
        return Result.error("查询失败");
    }

    @RequestMapping("selectTbItemParamItemByItemId")
    public Result selectTbItemParamItemByItemId(Long itemId){
        TbItemParamItem itemParam = itemServiceFeign.selectTbItemParamItemByItemId(itemId);
        if (itemParam != null){
            return Result.ok(itemParam);
        }
        return Result.error("查询失败");
    }
}
