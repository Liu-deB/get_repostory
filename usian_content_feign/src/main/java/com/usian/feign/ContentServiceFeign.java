package com.usian.feign;

import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-content-service")
public interface ContentServiceFeign {

    @PostMapping("service/contentCategory/selectContentCategoryByParentId")
    List<TbContentCategory> selectContentCategoryByParentId(@RequestParam("parentId") Long parentId);

    @RequestMapping("service/contentCategory/insertContentCategory")
    Integer insertContentCategory(@RequestBody TbContentCategory tbContentCategory);

    @RequestMapping("service/contentCategory/updateContentCategory")
    Integer updateContentCategory(@RequestBody TbContentCategory tbContentCategory);

    @RequestMapping("service/contentCategory/deleteContentCategoryById")
    Integer deleteContentCategoryById(@RequestParam Long categoryId);

    @RequestMapping("service/content/selectTbContentAllByCategoryId")
    PageResult selectTbContentAllByCategoryId(@RequestParam Long categoryId);

    @PostMapping("service/content/selectTbContentAllByCategoryId")
    PageResult selectTbContentAllByCategoryId(@RequestParam("page") Integer page,@RequestParam("rows") Integer rows,@RequestParam("categoryId") Long categoryId);

    @RequestMapping("service/content/insertTbContent")
    Integer insertTbContent(@RequestBody TbContent tbContent);

    @RequestMapping("service/content/deleteContentByIds")
    Integer deleteContentByIds(@RequestParam Long id);

    @RequestMapping("service/content/selectFrontendContentByAD")
    List<AdNode> selectFrontendContentByAD();
}