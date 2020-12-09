package cn.edu.xmu.activity.service.impl;

import cn.edu.xmu.activity.dao.CouponDao;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import cn.edu.xmu.oomall.goods.service.IActivityService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class IActivityServiceImpl implements IActivityService {

    @Autowired
    private CouponDao couponDao;

    @Override
    public List<CouponInfoDTO> getCouponInfoBySkuId(Long skuId) {
        List<CouponInfoDTO> couponInfoDTOs= couponDao.getCouponInfoBySkuId(skuId);
        return couponInfoDTOs;
    }
}
