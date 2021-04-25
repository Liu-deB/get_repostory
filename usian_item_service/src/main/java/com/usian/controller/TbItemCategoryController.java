package com.usian.controller;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.service.TbItemCategoryService;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("service/itemCategory")
public class TbItemCategoryController {

    @Autowired
    private TbItemCategoryService tbItemCatService;


    @RequestMapping("selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long id){
        return tbItemCatService.selectItemCategoryByParentId(id);
    }


    @RequestMapping("selectItemCategoryAll")
    public CatResult selectItemCategoryAll(){
        return tbItemCatService.selectItemCategoryAll();
    }
}
