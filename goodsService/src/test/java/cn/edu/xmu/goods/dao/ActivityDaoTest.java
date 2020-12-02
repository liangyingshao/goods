package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.model.vo.CouponSpuRetVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {

    @Autowired
    private ActivityDao activityDao;
    //数据库里还没有值，无法测试
//    @Test
//    void createCouponSpu()
//    {
//        ReturnObject<CouponSpuRetVo> returnObject=activityDao.createCouponSpu((long)273)
//    }


//    @Test
//    void getCouponSpuList() {
//        activityDao.getCouponSpuList((long)273,1,5);
//    }
}