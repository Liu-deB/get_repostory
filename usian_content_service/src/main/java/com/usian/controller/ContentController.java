package com.usian.controller;

import com.usian.pojo.TbContent;
import com.usian.service.ContentServiceImpl;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("service/content")
public class ContentController {
    @Autowired
    private ContentServiceImpl contentService;

    @RequestMapping("selectTbContentAllByCategoryId")
    public PageResult selectTbContentAllByCategoryId(@RequestParam Integer page,@RequestParam Integer rows, @RequestParam Long categoryId) {
        return contentService.selectTbContentAllByCategoryId(page, rows, categoryId);
    }

    @RequestMapping("insertTbContent")
    public Integer insertTbContent(@RequestBody TbContent tbContent){
        return contentService.insertTbContent(tbContent);
    }

    @RequestMapping("deleteContentByIds")
    public Integer deleteContentByIds(@RequestParam Long id){
        return contentService.deleteContentByIds(id);
    }

    @RequestMapping("selectFrontendContentByAD")
    public List<AdNode> selectFrontendContentByAD(){
        return contentService.selectFrontendContentByAD();
    }
}
