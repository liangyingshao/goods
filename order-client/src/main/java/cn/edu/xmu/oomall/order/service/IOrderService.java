package cn.edu.xmu.oomall.order.service;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.*;

import java.util.List;

/**
 * @author Caixin
 * @date 2020-12-05 21:37
 */
public interface IOrderService {

    /**
     * 由商品模块调用 下线团购时，通知订单模块将团购订单转为普通订单
     */
    ReturnObject<Object> putGrouponOffshelves(Long grouponId);

    /**
     * 由商品模块调用 下线预售时，通知订单模块对预售订单进行操作
     */
    ReturnObject<Object> putPresaleOffshevles(Long presaleId);

    ReturnObject<List<Long>> listUserSelectOrderItemIdBySkuList(Long userId, List<Long> skuId);

    /* 由商品模块调用
     * 根据shopId查询该商铺的所有订单详情表，并根据skuId的list筛选orderItemId，返回orderItemId的List
     */
    ReturnObject<List<Long>> listAdminSelectOrderItemIdBySkuList(Long shopId, List<Long> skuId);

    ReturnObject<Boolean> isOrderBelongToShop(Long shopId, Long orderId);

    //ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Integer quantity);

    ReturnObject<ResponseCode> getAdminHandleExchange(Long userId, Long shopId, Long orderItemId, Integer quantity, Long aftersaleId);

    /*由商品模块调用 支付完拆单
     */
    ReturnObject<ResponseCode> splitOrders(Long orderId);

    /**
     * 由商品模块调用，根据运费模板id返回简单运费模板
     */
    ReturnObject<SimpleFreightModelDTO> getSimpleFreightById(Long freightId);

    /**
     * 由商品模块调用：团购活动结束，商品模块调用此接口，订单用于退团购优惠金额
     */
    ReturnObject<Object> grouponEnd(String strategy,Long GrouponId);


}
