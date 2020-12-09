package cn.edu.xmu.activity.test;

import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.dao.CouponDao;
import cn.edu.xmu.activity.model.bo.CouponSku;
import cn.edu.xmu.activity.model.po.CouponSkuPo;
import cn.edu.xmu.activity.model.vo.CouponNewRetVo;
import cn.edu.xmu.activity.model.vo.CouponRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
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
class CouponDaoTest {

    @Autowired
    private CouponDao couponDao;

    @Test
    void getCouponSkuList()
    {
        List<CouponSkuPo> skus= couponDao.getCouponSkuList((long)1);
        Assertions.assertEquals(skus.size(),3);
    }

    @Test
    void createCouponSkus() {
        List<CouponSku> couponSkus=new ArrayList<>();
        CouponSku couponSku=new CouponSku();
        couponSku.setSkuId((long)273);
        couponSku.setGmtCreate(LocalDateTime.now());
        couponSku.setGmtModified(LocalDateTime.now());
        couponSkus.add(couponSku);
        ReturnObject returnObject= couponDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.COUPONACT_STATENOTALLOW);

        couponSkus.remove(0);
        couponSku.setActivityId((long)3);
        couponSkus.add(couponSku);
        returnObject= couponDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.OK);

        returnObject= couponDao.createCouponSkus((long)1, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.RESOURCE_ID_OUTSCOPE);

        couponSkus.remove(0);
        couponSku.setSkuId((long)1);
        couponSkus.add(couponSku);
        returnObject= couponDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.RESOURCE_ID_NOTEXIST);

        couponSkus.remove(0);
        couponSku.setActivityId((long)100);
        couponSkus.add(couponSku);
        returnObject= couponDao.createCouponSkus((long)0, (long)1, couponSkus);
        Assertions.assertEquals(returnObject.getCode(), ResponseCode.RESOURCE_ID_NOTEXIST);
    }

    @Test
    void deleteCouponSku()
    {
        ReturnObject returnObject= couponDao.deleteCouponSku((long)0,(long)5);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);

        returnObject= couponDao.deleteCouponSku((long)1,(long)5);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject= couponDao.deleteCouponSku((long)1,(long)5);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);

        returnObject= couponDao.deleteCouponSku((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPONACT_STATENOTALLOW);
    }

    @Test
    void showCoupons()
    {
        couponDao.getCoupon((long)1,(long)1);
        couponDao.getCoupon((long)1,(long)1);

        PageInfo<CouponRetVo> retVos= couponDao.showCoupons((long)1,1,1,2);
        Assertions.assertEquals(retVos.getList().size(),2);

        retVos= couponDao.showCoupons((long)1,1,2,1);
        Assertions.assertEquals(retVos.getList().size(),1);

        retVos= couponDao.showCoupons((long)1,1,1,5);
        Assertions.assertEquals(retVos.getList().size(),2);
    }

    @Test
    void useCoupon()
    {
        ReturnObject returnObject= couponDao.getCoupon((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
        returnObject= couponDao.getCoupon((long)1,(long)1);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
        PageInfo<CouponRetVo> retVos= couponDao.showCoupons((long)1,1,1,2);
        Assertions.assertEquals(retVos.getList().size(),2);

        returnObject= couponDao.useCoupon((long)1,retVos.getList().get(0).getId());
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject= couponDao.useCoupon((long)1,retVos.getList().get(0).getId());
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_STATENOTALLOW);

        returnObject= couponDao.useCoupon((long)1,(long)9999);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);

        returnObject= couponDao.useCoupon((long)9999,retVos.getList().get(1).getId());
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);
    }

    @Test
    void getCoupon()
    {
        ReturnObject<List<CouponNewRetVo>> returnObject= couponDao.getCoupon((long)1,(long)2);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject= couponDao.getCoupon((long)1,(long)2);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_FINISH);

        returnObject= couponDao.getCoupon((long)1,(long)3);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_NOTBEGIN);
    }

    @Test
    void returnCoupon()
    {
        ReturnObject returnObject= couponDao.returnCoupon((long) 1, (long)36);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.COUPON_STATENOTALLOW);

        couponDao.useCoupon((long)1,(long)36);
        returnObject= couponDao.returnCoupon((long) 1, (long)36);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject= couponDao.returnCoupon((long) 1, (long)9999);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);

        returnObject= couponDao.returnCoupon((long) 100, (long)38);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);

        returnObject= couponDao.returnCoupon((long) 1, (long)38);
        Assertions.assertEquals(returnObject.getCode(),ResponseCode.OK);
    }
}