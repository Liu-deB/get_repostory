package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("service/contentCategory")
public class ContentCategoryController {
    @Autowired
    private ContentCategoryServiceImpl contentCategoryService;

    /**
     * 根据父节点 ID 查询子节点
     */
    @RequestMapping("selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(@RequestParam Long parentId) {
        return contentCategoryService.selectContentCategoryByParentId(parentId);
    }

    @RequestMapping("insertContentCategory")
    public Integer insertContentCategory(@RequestBody TbContentCategory tbContentCategory){
        return contentCategoryService.insertContentCategory(tbContentCategory);
    }

    @RequestMapping("updateContentCategory")
    public Integer updateContentCategory(@RequestBody TbContentCategory tbContentCategory){
        return contentCategoryService.updateContentCategory(tbContentCategory);
    }

    @RequestMapping("deleteContentCategoryById")
    public Integer deleteContentCategoryById(@RequestParam Long categoryId){
        return contentCategoryService.deleteContentCategoryById(categoryId);
    }
}