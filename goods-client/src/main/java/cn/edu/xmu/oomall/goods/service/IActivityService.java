package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import cn.edu.xmu.oomall.goods.model.GoodsDetailDTO;
import cn.edu.xmu.oomall.goods.model.PresaleDTO;

import java.util.List;

/**
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-08 13:28
 * version: 1.0
 */
public interface IActivityService {
    List<CouponInfoDTO> getCouponInfoBySkuId(Long skuId);

    /**
     * 判断三个Id是否有效
     */
    ReturnObject<Boolean> judgeActivityIdValid(Long couponId, Long presaleId, Long grouponId);

    /**
     * 判断Id是否有效
     */
    ReturnObject<Boolean> judgeCouponActivityIdValid(Long couponActivityId);

    ReturnObject<Boolean> judgeCouponIdValid(Long couponId);

    ReturnObject<PresaleDTO> judgePresaleIdValid(Long presaleId);

    ReturnObject<Boolean> paymentPresaleIdValid(Long presaleId);

    ReturnObject<Boolean> judgeGrouponIdValid(Long grouponId);

    /**
     * 买家使用自己某优惠券
     */
    ReturnObject<ResponseCode> useCoupon(Long userId, Long id);

    /**
     * 优惠券退回
     */
    ReturnObject<ResponseCode> returnCoupon(Long shopId, Long id);

    ReturnObject<GoodsDetailDTO> modifyPresaleInventory(Long activityId, Integer quantity);

    /**
     * 根据activityId获得优惠活动Json字符串
     */
    ReturnObject<List<String>> getActivityRule(Long couponId,List<Long> activityId);

}