package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class ContentCategoryController {

    @Autowired
    private ContentServiceFeign contentServiceFeign;
    /**
     * 根据当前节点 ID 查询子节点
     */
    @RequestMapping("selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id) {
        List<TbContentCategory> list = contentServiceFeign.selectContentCategoryByParentId(id);
        if (list != null && list.size() > 0) {
            return Result.ok(list);
        }
        return Result.error("查无结果");
    }

    @RequestMapping("insertContentCategory")
    public Result insertContentCategory(TbContentCategory tbContentCategory){
        Integer integer = contentServiceFeign.insertContentCategory(tbContentCategory);
        if (integer == 1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    @RequestMapping("updateContentCategory")
    public Result updateContentCategory(TbContentCategory tbContentCategory){
        Integer integer = contentServiceFeign.updateContentCategory(tbContentCategory);
        if (integer == 1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    @RequestMapping("deleteContentCategoryById")
    public Result deleteContentCategoryById(Long categoryId){
        Integer integer = contentServiceFeign.deleteContentCategoryById(categoryId);
        if (integer == 1){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}