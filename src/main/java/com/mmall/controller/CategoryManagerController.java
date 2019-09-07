package com.mmall.controller;

import com.mmall.annotation.ManagerAnnotation;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.RoleCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/categoryManager")
public class CategoryManagerController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "/add_category.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation
   public ServerResponse<String> addCategory(HttpSession session,String categoryName, @RequestParam(defaultValue = "0") Integer parentId){

       if (StringUtils.isBlank(categoryName)||parentId==null){
           return ServerResponse.createByErrorMessage("参数错误");
       }
       return categoryService.addCategory(categoryName, parentId);
   }
    @RequestMapping(value = "/set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<String> updateCategoryName(HttpSession session,Integer categoryId,String categoryName){
        if (StringUtils.isBlank(categoryName)||categoryId==null){
            return ServerResponse.createByErrorMessage("更新类别参数错误");
        }
        return categoryService.updateCategory(categoryName, categoryId);
    }
    @RequestMapping(value = "/get_category.do",method = RequestMethod.GET)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<List<Category>> getChilrParallelCategory(HttpSession session, @RequestParam(defaultValue = "0") Integer categoryId){
       //获取传来参数的同级子节点
        return categoryService.getChildParallelCategory(categoryId);
    }
    @RequestMapping(value = "/get_deep_category.do",method = RequestMethod.GET)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<Set<Integer>> getChilrdDeepCategory(HttpSession session, @RequestParam(defaultValue = "0") Integer categoryId){
        //获取传来参数的所有子节点id
       return categoryService.getChildDeepCategory(categoryId);
    }
}
