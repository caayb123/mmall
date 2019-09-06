package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.CategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(String categoryName,Integer parentId) {
        Category category=new Category();
        category.setStatus(true);
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        int result = categoryMapper.insert(category);
        if (result==1){
            return ServerResponse.createBySuccessMessage("商品分类添加成功");
        }
      return ServerResponse.createByErrorMessage("商品分类添加失败");
    }

    @Override
    public ServerResponse<String> updateCategory(String categoryName, Integer categoryId) {
        Category category=new Category();
        category.setUpdateTime(new Date());
        category.setId(categoryId);
        category.setName(categoryName);
        int result = categoryMapper.updateByPrimaryKeySelective(category);
        if (result==1){
            return ServerResponse.createBySuccessMessage("分类名称修改成功");
        }
        return ServerResponse.createByErrorMessage("商品分类名称修改失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId) {
        Category category=new Category();
        category.setParentId(categoryId);
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(list)){
            return ServerResponse.createByErrorMessage("未找到该品类的分类");
        }
        return ServerResponse.createBySuccess(list);
    }

    @Override
    public ServerResponse<Set<Integer>> getChildDeepCategory(Integer categoryId) {
         Set<Integer> set=new HashSet<>();
         return ServerResponse.createBySuccess(findChildCategory(set,categoryId));
    }
    //递归查询子节点并且用set去重
    private Set<Integer> findChildCategory(Set<Integer> set,Integer categoryId){
        if (categoryId!=0){
            set.add(categoryId);
        }
        Category category=new Category();
        category.setParentId(categoryId);
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isNotEmpty(list)){
            for (Category item:list) {
                findChildCategory(set,item.getId());
            }
        }
        return set;
    }
}
