package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.SimpleFreightModelDTO;

import java.util.List;
import java.util.Map;

/**
 * @author Caixin
 * @date 2020-12-05 21:37
 */
public interface IOrderService {

    /**
     * 通过订单id查找用户id
     * @param orderId
     * @return Long
     * @author Cai Xinlu
     * @date 2020-12-05 21:38
     */

    /**
     * 根据userId查询该用户的订单详情表，并根据skuId的list筛选orderItemId，返回orderItemId的List
     */
    ReturnObject<List<Long>> listUserSelectOrderItemIdBySkuList(Long userId, List<Long> skuId);

    /**
     * 根据shopId查询该商铺的所有订单详情表，并根据skuId的list筛选orderItemId，返回orderItemId的List
     */
    ReturnObject<List<Long>> listAdminSelectOrderItemIdBySkuList(Long shopId, List<Long> skuId);

    ReturnObject<Boolean> isOrderBelongToShop(Long shopId, Long orderId);

    //ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Integer quantity);

    ReturnObject<ResponseCode> getAdminHandleExchange(Long userId, Long shopId, Long orderItemId, Integer quantity, Long aftersaleId);

    /**
     * 支付完拆单
     * @param orderId
     * @return
     */
    ReturnObject<ResponseCode> splitOrders(Long orderId);

    ReturnObject<SimpleFreightModelDTO> getSimpleFreightById(Long freightId);
}
