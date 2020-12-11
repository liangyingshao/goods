package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;

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

    /**
     * 判断Id是否有效
     * @param couponActivityId
     * @return Boolean
     * @author Cai Xinlu
     * @date 2020-12-09 17:04
     */
    ReturnObject<Boolean> judgeCouponActivityIdValid(Long couponActivityId);
}
