package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.oomall.order.model.SimpleFreightModelDTO;

import java.util.List;
import java.util.Map;

public interface IOrderService {

    /**
     * 根据orderItemId查询订单详情表和订单表信息，同时验证该orderItem是否属于该用户
     */
    ReturnObject<OrderDTO> getUserSelectSOrderInfo(Long userId, Long orderItemId);
    /**
     * 根据orderItemId查询订单详情表和订单表信息，同时验证该orderItem是否属于该商店
     * shopId为0时表示管理员 无需验证
     */
    ReturnObject<OrderDTO> getShopSelectOrderInfo(Long shopId, Long orderItemId);
    /**
     * 根据orderItemIdList查询订单详情表和订单表信息，同时验证该orderItem是否属于该店铺，返回orderItemId为key的Map
     * shopId为0时表示管理员 无需验证
     */
    ReturnObject<Map<Long,OrderDTO>> getShopSelectOrderInfoByList(Long shopId, List<Long> orderItemIdList);
    /**
     * 根据orderItemIdList查询订单详情表和订单表信息，同时验证该orderItem是否属于该用户，返回orderItemId为key的Map
     */
    ReturnObject<Map<Long,OrderDTO>> getUserSelectOrderInfoByList(Long userId, List<Long> orderItemIdList);
    /**
     * 根据userId查询该用户的订单详情表，并根据skuId的list筛选orderItemId，返回orderItemId的List
     */
    ReturnObject<List<Long>> listUserSelectOrderItemIdBySkuList(Long userId, List<Long> skuId);
    /**
     * 根据shopId查询该商铺的所有订单详情表，并根据skuId的list筛选orderItemId，返回orderItemId的List
     */
    ReturnObject<List<Long>> listAdminSelectOrderItemIdBySkuList(Long shopId, List<Long> skuId);
    /**
     * 换货api，产生orderitemId的新订单，商品数量为quantity
     */
    ReturnObject<ResponseCode> getAdminHandleExchange(Long userId, Long shopId, Long orderItemId, Integer quantity, Long aftersaleId);

    /**
     * 由商品模块调用 下线团购时，通知订单模块将团购订单转为普通订单
     */
    ReturnObject<Object> putGrouponOffshelves(Long grouponId);

    /**
     * 由商品模块调用 下线预售时，通知订单模块对预售订单进行操作
     */
    ReturnObject<Object> putPresaleOffshevles(Long presaleId);

    /*
     * 由商品模块调用 支付完拆单
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

    /**
     * 通过订单id查找用户id
     */
    ReturnObject<OrderInnerDTO> findUserIdbyOrderId(Long orderId);

    /**
     * 通过订单id查找shopId
     */
    ReturnObject<OrderInnerDTO> findShopIdbyOrderId(Long orderId);

    /**
     * 通过订单详情id查找订单id
     */
    ReturnObject<OrderInnerDTO> findOrderIdbyOrderItemId(Long orderItemId);

    /**
     * 判断订单是否属于某个商铺
     */
    ReturnObject<Boolean> isOrderBelongToShop(Long shopId, Long orderId);

    /**
     * 通过OrderItemId获取OrderId
     */
    ReturnObject<Long> getOrderIdByOrderItemId(Long orderId);
}
