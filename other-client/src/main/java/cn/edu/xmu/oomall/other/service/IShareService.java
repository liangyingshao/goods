package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分享服务调用接口
 * @author Fiber W.
 * created at 12/6/20 8:26 PM
 * @detail cn.edu.xmu.oomall.other.service
 */
public interface IShareService {
    /**
     * 设置分享返点
     * @param orderItemId 订单详细id
     * @param userId 用户id
     * @param quantity 购买数量
     * @param price 单价
     * @param skuId 商品skuid
     * @param gmtCreated 下单时间
     * @return cn.edu.xmu.oomall.util.ReturnObject<java.util.List<java.lang.Long>>
     * @author Fiber W.
     * created at 12/6/20 8:29 PM
     */
    ReturnObject<List<Long>> setShareRebate(Long orderItemId, Long userId, Integer quantity, Long price, Long skuId, LocalDateTime gmtCreated);
}
