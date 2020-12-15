package cn.edu.xmu.oomall.order.service;


import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

public interface IPaymentService {

    /**
     * 退款api，产生orderitemId的退款，refund为退款数，负数
     */
    ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId);
    
    /**
     * 维修api，产生orderitemId的新支付单，refund为付款数，正数
     */
    ReturnObject<ResponseCode> getAdminHandleRepair(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId);
}
