package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 内容分类业务层
 */
@Service
public class ContentCategoryServiceImpl implements Serializable {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    /**
     * 根据父节点 Id 查询子节点
     *
     * @param parentId
     * @return
     */
    public List<TbContentCategory> selectContentCategoryByParentId(Long parentId) {
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        criteria.andStatusEqualTo(1);
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        return list;
    }

    public Integer insertContentCategory(TbContentCategory tbContentCategory){
        Date date = new Date();
        tbContentCategory.setCreated(date);
        tbContentCategory.setUpdated(date);
        tbContentCategory.setStatus(1);
        return tbContentCategoryMapper.insertSelective(tbContentCategory);
    }

    public Integer updateContentCategory(TbContentCategory tbContentCategory){
        Date date = new Date();
        tbContentCategory.setCreated(date);
        tbContentCategory.setUpdated(date);
        tbContentCategory.setStatus(1);
        return tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
    }

        public Integer deleteContentCategoryById(Long categoryId) {
            // 根据ID查询出分类
            TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
            // 判断当前分类是否是根，如果是根分类则拒绝删除，如果不是则走删除逻辑
            if(tbContentCategory.getParentId() == 0){
                // 因为当前分类为根分类，所以拒绝删除
                return 0;
            }
            // 删除当前分类的子分类
            this.deleteContentCategoryByParentId(categoryId);
            // 删除分类
            this.deleteById(categoryId);
            return 1;
        }

        private void deleteContentCategoryByParentId(Long parentId){
            // 根据父级ID查询所有子分类
            List<TbContentCategory> list = this.getContentCategoryByParentId(parentId);
            for (TbContentCategory tbContentCategory : list) {
                List<TbContentCategory> clist = this.getContentCategoryByParentId(tbContentCategory.getId());
                // 判断如果该分类有子分类
                if(clist != null && clist.size() > 0){
                    this.deleteContentCategoryByParentId(tbContentCategory.getId());
                }
                // 删除当前分类
                this.deleteById(tbContentCategory.getId());
            }
        }

        private List<TbContentCategory> getContentCategoryByParentId(Long parentId){
            TbContentCategoryExample example  = new TbContentCategoryExample();
            TbContentCategoryExample.Criteria criteria = example.createCriteria();
            criteria.andParentIdEqualTo(parentId); // 最后生成为 where XXX and parentID = 传入值
            criteria.andStatusEqualTo(1);
            return tbContentCategoryMapper.selectByExample(example);
        }

        private void deleteById(Long id){
        // 删除分类
        TbContentCategory t = new TbContentCategory();
        t.setId(id);
        t.setStatus(0);
        tbContentCategoryMapper.updateByPrimaryKeySelective(t);
    }
}