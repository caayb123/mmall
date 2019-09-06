package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    ServerResponse<String> addCategory(String categoryName,Integer parentId);

    ServerResponse<String> updateCategory(String categoryName,Integer categoryId);

    ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId);

    ServerResponse<Set<Integer>> getChildDeepCategory(Integer categoryId);
}
