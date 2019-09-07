package com.mmall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.mmall.annotation.ManagerAnnotation;
import com.mmall.common.Const;
import com.mmall.common.ManagerCode;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping(value = "/order")
public class OrderController {
    private static final Logger log= LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;
    @RequestMapping(value = "/create.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse createOrder(HttpSession session,Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.createOrder(user.getId(),shippingId);
    }
    @RequestMapping(value = "/cancel.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse cancelOrder(HttpSession session,Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.cancelOrder(user.getId(),orderNo);
    }
    @RequestMapping(value = "/get_order_cart_product.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.getOrderCartProduct(user.getId());
    }
    @RequestMapping(value = "/detail.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse detail(HttpSession session,Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.detail(user.getId(),orderNo);
    }
    @RequestMapping(value = "/list.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(defaultValue = "1")Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    @RequestMapping(value = "/pay.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        String path = request.getServletContext().getRealPath("code");
        return orderService.pay(user.getId(),orderNo,path);
    }
    @RequestMapping(value = "/callBackPay.do")
    @ResponseBody
    public Object callBack(HttpServletRequest request){
        Map<String,String> map=new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Iterator<String> iterator = parameterMap.keySet().iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            String[] values = parameterMap.get(name);
            String value="";
            for (int i=0;i<values.length;i++){
                value=(i==values.length-1)?value+values[i]:value+values[i]+",";
            }
             map.put(name,value);
        }
       log.info("支付宝回调,sign:"+map.get("sign")+"trade_status:"+map.get("trade_status")+"参数"+map.toString());
        //验证回调正确性,去除重复通知.
        map.remove("sign_type");
        try {
            //支付宝RSA解密验证回调是否是支付宝官方
            boolean checkV2 = AlipaySignature.rsaCheckV2(map, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!checkV2){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }
        ServerResponse serverResponse = orderService.checkPay(map);    //重复通知判断
        if (serverResponse.isSuccess()){
            return Const.RESPONSE_SUCCESS;
        }
        return Const.RESPONSE_FAILED;
    }
    @RequestMapping(value = "/query_order_pay_status.do",method = RequestMethod.POST)
    @ResponseBody
    @ManagerAnnotation(value = ManagerCode.NOAUTHORITY)
    public ServerResponse queryPayStatus(HttpSession session, Long orderNo){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        return orderService.queryStatus(user.getId(), orderNo);
    }
}
