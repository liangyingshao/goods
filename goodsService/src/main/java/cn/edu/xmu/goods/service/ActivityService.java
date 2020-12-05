package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.ActivityDao;
import cn.edu.xmu.goods.model.bo.CouponActivity;
import cn.edu.xmu.goods.model.bo.CouponSpu;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {
    private Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private ActivityDao activityDao;

    /**
     * 查看优惠活动中的商品
     * @param id
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<CouponSpu>>
     */
    @Transactional
    public ReturnObject<PageInfo<GoodsSpuCouponRetVo>> getCouponSpuList(Long id, Integer page, Integer pageSize) {

        PageInfo<GoodsSpuCouponRetVo>couponSpus=activityDao.getCouponSpuList(id,page,pageSize);
        return new ReturnObject<>(couponSpus);
    }

    /**
     * 管理员为己方某优惠券活动新增限定范围
     * @param shopId
     * @param couponSpu
     * @return ReturnObject<CouponSpuRetVo>
     */
    @Transactional
    public ReturnObject<CouponSpuRetVo> createCouponSpu(Long shopId, CouponSpu couponSpu) {
        ReturnObject<CouponSpuRetVo> returnObject=activityDao.createCouponSpu(shopId,couponSpu);
        return returnObject;
    }

    /**
     * 店家删除己方某优惠券活动的某限定范围
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject deleteCouponSpu(Long shopId, Long id)
    {
        ReturnObject returnObject=activityDao.deleteCouponSpu(shopId,id);
        return returnObject;
    }

    /**
     * 买家查看优惠券列表
     * @param userId
     * @param state
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<CouponRetVo>>
     */
    @Transactional
    public ReturnObject<PageInfo<CouponRetVo>> showCoupons(Long userId, Integer state, Integer page, Integer pageSize)
    {
        PageInfo<CouponRetVo> returnObject=activityDao.showCoupons(userId,state,page,pageSize);
        return new ReturnObject<PageInfo<CouponRetVo>>(returnObject);
    }

    /**
     * 买家使用自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject useCoupon(Long userId, Long id)
    {
        ReturnObject returnObject=activityDao.useCoupon(userId,id);
        return returnObject;
    }

    //据说已废弃
    /**
     * 买家删除自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject deleteCoupon(Long userId, Long id)
    {
        ReturnObject returnObject=activityDao.deleteCoupon(userId,id);
        return returnObject;
    }

    /**
     * 买家领取活动优惠券
     * @param userId
     * @param id
     * @return ReturnObject<CouponNewRetVo>
     */
    @Transactional
    public ReturnObject<CouponNewRetVo> getCoupon(Long userId, Long id)
    {
        ReturnObject<CouponNewRetVo> returnObject=activityDao.getCoupon(userId,id);
        return returnObject;
    }

    /**
     * 优惠券退回
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject returnCoupon(Long id)
    {
        ReturnObject returnObject=activityDao.returnCoupon(id);
        return returnObject;
    }

    /**
     * 店家查询己方某优惠券活动
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<Object> showCouponActivity(Long shopId, Long id) {
        ReturnObject returnObject=activityDao.showCouponActivity(shopId,id);
        return returnObject;
    }

    /**
     * 管理员新建己方优惠活动
     * @param activity
     * @return ReturnObject
     */
    public ReturnObject addCouponActivity(CouponActivity activity) {
        ReturnObject<CouponActivityVo> returnObject=activityDao.addCouponActivity(activity);
        return returnObject;
    }
}
