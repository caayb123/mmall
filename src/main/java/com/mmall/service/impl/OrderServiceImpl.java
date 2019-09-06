package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Ordering;
import com.mmall.common.*;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.OrderService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.DateUtil;
import com.mmall.utils.OssUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private static final Logger log= LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Override
    public ServerResponse<Map<String, String>> pay(Integer userId, Long orderNo,String path) {
        Map<String, String> map=new HashMap<>();
        if (orderNo==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Order order=orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        map.put("orderNo",order.getOrderNo().toString());
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder("mmall商城扫码支付,订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder("订单").append(outTradeNo).append("购买商品一共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        OrderItem orderItem=new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setOrderNo(orderNo);
        List<OrderItem> orderItemList=orderItemMapper.select(orderItem);
        for (OrderItem item : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(item.getProductId().toString(), item.getProductName(), BigDecimalUtil.mul(item.getCurrentUnitPrice().doubleValue(),100d).longValue(), item.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://39.96.59.137:8081/mmall/order/callBackPay.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");
        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().setCharset("utf-8").build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                File folder=new File(path);
                if (!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdir();
                }
                // 需要修改为运行机器上的路径
                String filePath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                try {
                    String imageUrl = OssUtil.getImageUrl(new FileInputStream(new File(filePath)));
                    map.put("qrUrl",imageUrl);
                } catch (Exception e) {
                    log.info("二维码上传异常",e);
                }
                log.info("filePath:" + filePath);
               return ServerResponse.createBySuccess(map);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }

    }

    @Override
    public ServerResponse checkPay(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));  //获取支付订单号
        String tradeNo = params.get("trade_no");   //支付宝交易号(非订单号)
        String tradeStatus = params.get("trade_status");  //支付状态

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("非网站订单，回调忽略");
        }
        if (order.getStatus()>= AlipayCode.PAID.getCode()){
            //订单状态属于已付款或已付款后的状态
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }
        if (Const.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            //支付宝回调参数告知已经付款成功，也需要将订单更改为已付款
            order.setPaymentTime(DateUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(AlipayCode.PAID.getCode());
            orderMapper.updateStatus(order);
        }
        PayInfo payInfo=new PayInfo();  //组装支付信心对象
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(PayPlatfromCode.ALIPAY.getCode());
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        payInfo.setOrderNo(orderNo);
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse queryStatus(Integer userId, Long orderNo) {
        if (orderNo==null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        Order order=orderMapper.selectByUserIdOrderNo(userId, orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus()>= AlipayCode.PAID.getCode()){
            //只要是订单状态是已付款或之上都认为是付款成功
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByEroor();
    }

    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        if (shippingId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Cart cart=new Cart();
        cart.setUserId(userId);
        cart.setChecked(CartCode.CHECKED.getCode());
        List<Cart> carts = cartMapper.select(cart);
        //获取购物车内选中商品的订单详情
        ServerResponse<List<OrderItem>> serverResponse = getListOrderItem(carts);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        //遍历商品详情计算商品总价
        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal totalPrice=new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
          totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
       //生成订单号
        long orderNo = createOrderNo();
        //开始组建订单
        Order order=new Order();
        order.setOrderNo(orderNo);
        order.setPostage(0);  //暂时全场包邮
        order.setStatus(AlipayCode.NO_PAY.getCode());
        order.setPayment(totalPrice); //支付金额
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPaymentType(PayPlatfromCode.ONLINE_PAY.getCode());//支付方式
        int result = orderMapper.insertOne(order);
        if (result>0){
           //给单个商品订单详情插入生成的订单号
            for (OrderItem orderItem : orderItemList) {
                orderItem.setOrderNo(orderNo);
            }
            //批量插入单个商品订单详情
          orderItemMapper.batchInsert(orderItemList);
            //减少商品库存
            for (OrderItem orderItem : orderItemList) {
                Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
                product.setStock(product.getStock()-orderItem.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);
            }
          //清空购物车
            for (Cart cart1 : carts) {
                cartMapper.deleteByPrimaryKey(cart1.getId());
            }
            //返回给前端的数据
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }else {
          return   ServerResponse.createByErrorMessage("生成订单失败");
        }
    }

    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo) {
        if (orderNo==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //先检查订单是否未支付,支付过的不能取消
        Order order = orderMapper.selectByUserIdOrderNo(userId, orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("用户没有此订单");
        }
        if (order.getStatus()>10){
            return ServerResponse.createByErrorMessage("此订单已付款无法被取消");
        }
        //取消订单状态
        int result = orderMapper.updateStatusById(AlipayCode.CANCELED.getCode(), order.getOrderNo());
        if (result>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByEroor();
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
        //这个功能就是购物车结算的时候拿到所有被选中的商品明细
        Cart cart=new Cart();
        cart.setUserId(userId);
        cart.setChecked(CartCode.CHECKED.getCode());
        List<Cart> carts = cartMapper.select(cart);
        ServerResponse<List<OrderItem>> serverResponse = getListOrderItem(carts);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        List<OrderItemVo> orderItemVoList = assembleOrderItemVo(orderItemList);
        BigDecimal totalPrice=new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        //组装购物车选中的商品明细
        OrderProductVo orderProductVo=new OrderProductVo();
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "https://cybimage.oss-cn-shenzhen.aliyuncs.com/"));
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(totalPrice);
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse<OrderVo> detail(Integer userId, Long orderNo) {
        if (orderNo==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Order order = orderMapper.selectByUserIdOrderNo(userId, orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("没有找到此订单");
        }
        OrderItem orderItem=new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setOrderNo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.select(orderItem);
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orders);
        PageInfo pageInfo=new PageInfo(orders);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> findAll(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.findAll();
        List<OrderVo> orderVoList = assembleOrderVoList(orders);
        PageInfo pageInfo=new PageInfo(orders);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<OrderVo> searchByOrderNo(Long orderNo) {
        if (orderNo==null){
            return ServerResponse.createByErrorMessage("参数有误");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("没有找到此订单");
        }
        OrderItem orderItem=new OrderItem();
        orderItem.setOrderNo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.select(orderItem);
        return ServerResponse.createBySuccess(assembleOrderVo(order,orderItemList));
    }

    @Override
    public ServerResponse updateSendStatus(Long orderNo) {
        if (orderNo==null){
            return ServerResponse.createByErrorMessage("参数有误");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
           if (AlipayCode.PAID.getCode()==order.getStatus()){
               order.setStatus(AlipayCode.SHIPPED.getCode());
               order.setSendTime(new Date());
               orderMapper.updateStatusSendTime(order);
               return ServerResponse.createBySuccess("发货成功");
           }else {
               return ServerResponse.createBySuccess("发货失败");
           }

        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    //组合返回给前端的抽象订单实体
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo=new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());  //设置订单号
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "https://cybimage.oss-cn-shenzhen.aliyuncs.com/"));//设置主图
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(PayPlatfromCode.getDescByCode(order.getPaymentType()));
        orderVo.setPaymentTime(DateUtil.dateToStr(order.getPaymentTime()));
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(AlipayCode.getDescByCode(order.getStatus()));
        orderVo.setCloseTime(DateUtil.dateToStr(order.getCloseTime()));
        orderVo.setCreateTime(DateUtil.dateToStr(order.getCreateTime()));
        orderVo.setEndTime(DateUtil.dateToStr(order.getEndTime()));
        orderVo.setSendTime(DateUtil.dateToStr(order.getSendTime()));
        //订单明细
         orderVo.setOrderItemVoList(assembleOrderItemVo(orderItemList));
        //收获地址
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping!=null){
            ShippingVo shippingVo = assembleShippingVo(shipping);
            orderVo.setShippingId(order.getShippingId());
            orderVo.setShippingVo(shippingVo);
            orderVo.setReceiverName(shipping.getReceiverName());
        }
        return orderVo;
    }
    //订单明细组合
    private List<OrderItemVo> assembleOrderItemVo(List<OrderItem> orderItemList){
        List<OrderItemVo> orderItemVoList=new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo=new OrderItemVo();
            orderItemVo.setCreateTime(DateUtil.dateToStr(orderItem.getCreateTime()));
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setOrderNo(orderItem.getOrderNo());
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setProductImage(orderItem.getProductImage());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
            orderItemVoList.add(orderItemVo);
        }
        return orderItemVoList;
    }
    //收货地址组合
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo=new ShippingVo();
        BeanUtils.copyProperties(shipping,shippingVo);
       return shippingVo;
    }

    //组合购物车选中的所以商品的单个商品的订单详情
    private ServerResponse<List<OrderItem>> getListOrderItem(List<Cart> cartList){
        List<OrderItem> list=new ArrayList<>();
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //开始校验购物车勾选商品数据
        for (Cart cart : cartList) {
            //先查出产品是否售卖和库存
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus()==ProductCode.OFF_SALE.getCode()){
                return ServerResponse.createByErrorMessage("产品:"+product.getName()+"已下架");
            }
            if (product.getStock()<cart.getQuantity()){
                return ServerResponse.createByErrorMessage("产品:"+product.getName()+"库存不足");
            }
            //开始组装单个商品的订单详情
            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(cart.getUserId());
            orderItem.setCreateTime(new Date());
            orderItem.setProductId(product.getId());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity()));
            list.add(orderItem);
        }
      return ServerResponse.createBySuccess(list);
    }
    //组合返回给前端的抽象实体的集合
    private List<OrderVo> assembleOrderVoList(List<Order> orderList) {
        List<OrderVo> orderVoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            for (Order order : orderList) {
                //查出每个订单的订单明细
                OrderItem orderItem = new OrderItem();
                orderItem.setUserId(order.getUserId());
                orderItem.setOrderNo(order.getOrderNo());
                orderVoList.add(assembleOrderVo(order, orderItemMapper.select(orderItem)));
            }
        }
        return orderVoList;
    }
    //生成订单号
    private long createOrderNo(){
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis+new Random().nextInt(100); //防止并发订单重复,需要生成0~99随机数
    }
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
