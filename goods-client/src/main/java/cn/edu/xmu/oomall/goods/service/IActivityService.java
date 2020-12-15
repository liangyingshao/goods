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
     * @param couponId presaleId grouponId
     * @return Boolean
     * @author Cai Xinlu
     * @date 2020-12-09 17:04
     */
    ReturnObject<Boolean> judgeActivityIdValid(Long couponId, Long presaleId, Long grouponId);

    ReturnObject<Boolean> judgeCouponIdValid(Long couponId);

    ReturnObject<PresaleDTO> judgePresaleIdValid(Long presaleId);

    ReturnObject<Boolean> paymentPresaleIdValid(Long presaleId);

    ReturnObject<Boolean> judgeGrouponIdValid(Long grouponId);
    /**
     * 判断Id是否有效
     * @param couponActivityId
     * @return Boolean
     * @author Cai Xinlu
     * @date 2020-12-09 17:04
     */
    ReturnObject<Boolean> judgeCouponActivityIdValid(Long couponActivityId);



    /**
     * 买家使用自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    ReturnObject<ResponseCode> useCoupon(Long userId, Long id);

    /**
     * 优惠券退回
     *
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    ReturnObject<ResponseCode> returnCoupon(Long shopId, Long id);

    ReturnObject<GoodsDetailDTO> modifyPresaleInventory(Long activityId, Integer quantity);

    /**
     * 根据activityId获得优惠活动Json字符串
     * @param activityId
     * @return RetrunObject<String>
     */
    ReturnObject<List<String>> getActivityRule(Long couponId,List<Long> activityId);

}
