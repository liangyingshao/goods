package cn.edu.xmu.activity.service.impl;

import cn.edu.xmu.activity.dao.CouponDao;
import cn.edu.xmu.activity.dao.GrouponDao;
import cn.edu.xmu.activity.dao.PresaleDao;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import cn.edu.xmu.oomall.goods.model.PresaleDTO;
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

    @Autowired
    private PresaleDao presaleDao;

    @Override
    public List<CouponInfoDTO> getCouponInfoBySkuId(Long skuId) {
        List<CouponInfoDTO> couponInfoDTOs= couponDao.getCouponInfoBySkuId(skuId);
        return couponInfoDTOs;
    }

    @Override
    public ReturnObject<Boolean> judgeActivityIdValid(Long couponId, Long presaleId, Long grouponId) {
        return null;
    }

    @Override
    public ReturnObject<Boolean> judgeCouponActivityIdValid(Long couponActivityId) {
        Boolean valid=couponDao.judgeCouponActivityIdValid(couponActivityId);
        return new ReturnObject<Boolean>(valid);
    }

    @Override
    public ReturnObject<Boolean> judgeCouponIdValid(Long couponId) {

        Boolean valid=couponDao.judgeCouponValid(couponId)
//                &&grouponDao.judgeGrouponValid(grouponId)&&presaleDao.judgePresaleValid(presaleId)
                ;

        return new ReturnObject<>(valid);
    }

    @Override
    public ReturnObject<Boolean> judgeGrouponIdValid(Long grouponId) {
        return grouponDao.judgeGrouponIdValid(grouponId);
    }

    @Override
    public ReturnObject<PresaleDTO> judgePresaleIdValid(Long presaleId) {//第一次下订单校验presaleId,需要校验时间
        return presaleDao.judgePresaleValid(presaleId);
    }
    @Override
    public ReturnObject<Boolean> paymentPresaleIdValid(Long presaleId) {//付尾款校验，需要校验时间
        return presaleDao.paymentPresaleIdValid(presaleId);
    }




    @Override
    public ReturnObject useCoupon(Long userId, Long id)
    {
        ReturnObject returnObject= couponDao.useCoupon(userId,id);
        return returnObject;
    }
    @Override
    public ReturnObject returnCoupon(Long shopId, Long id)
    {
        ReturnObject returnObject= couponDao.returnCoupon(shopId,id);
        return returnObject;
    }



}
