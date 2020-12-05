package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.goods.model.vo.GoodsSkuCouponRetVo;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(classes = GoodsServiceApplication.class)
@Transactional
class ActivityDaoTest {

    @Autowired
    private ActivityDao activityDao;

    @Test
    void getCouponSkuList()
    {
        PageInfo<GoodsSkuCouponRetVo> skus=activityDao.getCouponSkuList((long)1,1,2);
        assertEquals(skus.getList().size(),2);

        skus=activityDao.getCouponSkuList((long)1,2,1);
        assertEquals(skus.getList().size(),1);
    }

    @Test
    void createCouponSkus() {
    }

    @Test
    void deleteCouponSku() {
    }

    @Test
    void showCoupons() {
    }

    @Test
    void useCoupon() {
    }

    @Test
    void deleteCoupon() {
    }

    @Test
    void getCoupon() {
    }

    @Test
    void returnCoupon() {
    }
}