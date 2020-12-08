package cn.edu.xmu.activity.test;

import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.dao.ActivityDao;
import cn.edu.xmu.activity.model.bo.CouponSku;
import cn.edu.xmu.activity.model.po.CouponSkuPo;
import cn.edu.xmu.activity.model.vo.CouponNewRetVo;
import cn.edu.xmu.activity.model.vo.CouponRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.SkuNameInfoDTO;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(classes = ActivityServiceApplication.class)
@Transactional
class ActivityDaoTest {

    @Autowired
    private ActivityDao activityDao;

    @Test
    void getCouponSkuList()
    {
        List<CouponSkuPo> skus=activityDao.getCouponSkuList((long)1,1,2);
        Assertions.assertEquals(skus.size(),2);

        skus=activityDao.getCouponSkuList((long)1,2,1);
        Assertions.assertEquals(skus.size(),1);
    }

    @Test
    void createCouponSkus() {
        List<CouponSku> couponSkus=new ArrayList<>();
        CouponSku couponSku=new CouponSku();
        couponSku.setSkuId((long)273);
        couponSku.setGmtCreate(LocalDateTime.now());
        couponSku.setGmtModified(LocalDateTime.now());
        couponSkus.add(couponSku);
        ReturnObject returnObject=activityDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.COUPONACT_STATENOTALLOW);

        couponSkus.remove(0);
        couponSku.setActivityId((long)3);
        couponSkus.add(couponSku);
        returnObject=activityDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.OK);

        returnObject=activityDao.createCouponSkus((long)1, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.RESOURCE_ID_OUTSCOPE);

        couponSkus.remove(0);
        couponSku.setSkuId((long)1);
        couponSkus.add(couponSku);
        returnObject=activityDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.RESOURCE_ID_NOTEXIST);

        couponSkus.remove(0);
        couponSku.setActivityId((long)100);
        couponSkus.add(couponSku);
        returnObject=activityDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.RESOURCE_ID_NOTEXIST);
    }

    @Test
    void deleteCouponSku()
    {
        ReturnObject returnObject=activityDao.deleteCouponSku((long)0,(long)5);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);

        returnObject=activityDao.deleteCouponSku((long)1,(long)5);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=activityDao.deleteCouponSku((long)1,(long)5);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);

        returnObject=activityDao.deleteCouponSku((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPONACT_STATENOTALLOW);
    }

    @Test
    void showCoupons()
    {
        activityDao.getCoupon((long)1,(long)1);
        activityDao.getCoupon((long)1,(long)1);

        PageInfo<CouponRetVo> retVos=activityDao.showCoupons((long)1,1,1,2);
        Assertions.assertEquals(retVos.getList().size(),2);

        retVos=activityDao.showCoupons((long)1,1,2,1);
        Assertions.assertEquals(retVos.getList().size(),1);

        retVos=activityDao.showCoupons((long)1,1,1,5);
        Assertions.assertEquals(retVos.getList().size(),2);
    }

    @Test
    void useCoupon()
    {
        ReturnObject returnObject=activityDao.getCoupon((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
        returnObject=activityDao.getCoupon((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
        PageInfo<CouponRetVo> retVos=activityDao.showCoupons((long)1,1,1,2);
        Assertions.assertEquals(retVos.getList().size(),2);

        returnObject= activityDao.useCoupon((long)1,retVos.getList().get(0).getId());
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject= activityDao.useCoupon((long)1,retVos.getList().get(0).getId());
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_STATENOTALLOW);

        returnObject= activityDao.useCoupon((long)1,(long)9999);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);

        returnObject= activityDao.useCoupon((long)9999,retVos.getList().get(1).getId());
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);
    }

    //据说已废弃
//    @Test
//    void deleteCoupon()
//    {
//
//    }

    @Test
    void getCoupon()
    {
        ReturnObject<CouponNewRetVo> returnObject=activityDao.getCoupon((long)1,(long)2);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=activityDao.getCoupon((long)1,(long)2);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_FINISH);

        returnObject=activityDao.getCoupon((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
        returnObject=activityDao.getCoupon((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
        returnObject=activityDao.getCoupon((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_FINISH);

        returnObject=activityDao.getCoupon((long)1,(long)3);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_NOTBEGIN);
    }

    @Test
    void returnCoupon()
    {
        ReturnObject returnObject= activityDao.returnCoupon((long) 1, (long)36);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_STATENOTALLOW);

        activityDao.useCoupon((long)1,(long)36);
        returnObject= activityDao.returnCoupon((long) 1, (long)36);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=activityDao.returnCoupon((long) 1, (long)9999);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);

        returnObject=activityDao.returnCoupon((long) 100, (long)38);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);

        returnObject=activityDao.returnCoupon((long) 1, (long)38);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
    }
}