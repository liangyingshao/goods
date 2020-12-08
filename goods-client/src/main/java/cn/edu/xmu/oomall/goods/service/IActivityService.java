package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;

import java.util.List;

/**
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-08 13:28
 * version: 1.0
 */
public interface IActivityService {
    List<CouponInfoDTO> getCouponInfoBySkuId(Long skuId);
}
