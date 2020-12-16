package cn.edu.xmu.oomall.other.service;


import cn.edu.xmu.ooad.util.ReturnObject;

import java.time.LocalDateTime;
import java.util.List;

public interface IShareService {

    /**
     * 设置分享返点
     */
    ReturnObject<List<Long>> setShareRebate(Long orderItemId, Long userId, Integer quantity, Long price, Long skuId, LocalDateTime gmtCreated);

    /**
     * 判断sid和skuid是否匹配
     */
    ReturnObject shareUserSkuMatch(Long sid, Long skuId, Long userId);

    /**
     * 判断SKU是否可分享
     */
    ReturnObject<Boolean> skuSharable(Long skuId);

}
