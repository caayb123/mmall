package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.annotation.ManagerAnnotation;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.RoleCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.ProductService;
import com.mmall.utils.OssUtil;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/productManager")
public class ProductManagerController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<PageInfo> productList(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, HttpSession session){
        return productService.findAll(pageNum, pageSize);
    }
    @RequestMapping(value = "/save.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<String> saveOrUpdateProduct(HttpSession session,Product product){
        if (product==null){
            return ServerResponse.createByErrorMessage("添加产品的参数有误");
        }
        return productService.saveOrUpdateProduct(product);
    }
    @RequestMapping(value = "/set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<String> setSaleStatus(HttpSession session,Integer productId,Integer status){
        if (productId==null||status==null){
            return ServerResponse.createByErrorMessage("上下架产品参数有误");
        }
          return productService.setSaleStatus(productId, status);
    }
    @RequestMapping(value = "/detail.do",method = RequestMethod.GET)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<ProductDetailVo> detail(HttpSession session, Integer productId){
        if (productId==null){
            return ServerResponse.createByErrorMessage("获取商品参数有误");
        }
        return productService.getDetails(productId);
    }
    @RequestMapping(value = "/search.do",method = RequestMethod.GET)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse<PageInfo> search(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, HttpSession session,Integer productId,String productName){
        return productService.search(pageNum, pageSize, productName, productId);
    }
    @RequestMapping(value = "/upload.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation
    public ServerResponse upload(MultipartFile multipartFile,HttpSession session) throws Exception{
        String imageUrl = OssUtil.getImageUrl(multipartFile.getInputStream());
        if (imageUrl!=null) {
            return ServerResponse.createBySuccess(imageUrl);
        }else {
            return ServerResponse.createByErrorMessage("图片上传异常");
        }
    }
    @RequestMapping(value = "/richText_img_upload.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> richTextUpload(MultipartFile multipartFile, HttpServletResponse response, HttpSession session) throws Exception{
        Map<String,Object> map=new HashMap<>();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            map.put("success",false);
            map.put("msg","用户未登录,请登录");
            return map;
        }
        if (user.getRole()!= RoleCode.ROLE_ADMIN.getCode()){
            map.put("success",false);
            map.put("msg","用户无权限操作");
            return map;
        }
        String imageUrl = OssUtil.getImageUrl(multipartFile.getInputStream());
        if (imageUrl!=null) {
            map.put("success",true);
            map.put("msg","上传成功");
            map.put("file_path",imageUrl);
            //根据文档处理请求头
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return map;
        }else {
            map.put("success",false);
            map.put("msg","图片上传失败");
            return map;
        }
    }


}
