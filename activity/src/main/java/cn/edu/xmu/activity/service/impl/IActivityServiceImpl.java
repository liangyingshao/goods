package cn.edu.xmu.activity.service.impl;

import cn.edu.xmu.activity.dao.CouponDao;
import cn.edu.xmu.activity.dao.GrouponDao;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import cn.edu.xmu.oomall.goods.service.IActivityService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class IActivityServiceImpl implements IActivityService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private GrouponDao grouponDao;

    @Override
    public List<CouponInfoDTO> getCouponInfoBySkuId(Long skuId) {
        List<CouponInfoDTO> couponInfoDTOs= couponDao.getCouponInfoBySkuId(skuId);
        return couponInfoDTOs;
    }

    @Override
    public ReturnObject<Boolean> judgeActivityIdValid(Long couponId, Long presaleId, Long grouponId) {
        Boolean valid=couponDao.judgeCouponValid(couponId)
//                &&grouponDao.judgeGrouponValid(grouponId)&&presaleDao.judgePresaleValid(presaleId)
                ;

        return new ReturnObject<>(valid);
    }

    @Override
    public ReturnObject<Boolean> judgeCouponActivityIdValid(Long couponActivityId) {
        return null;
    }
}
