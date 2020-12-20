//package cn.edu.xmu.activity.service.mq;
//
//import cn.edu.xmu.activity.dao.CouponDao;
//import cn.edu.xmu.activity.model.bo.Coupon;
//import cn.edu.xmu.activity.model.po.CouponPo;
//import cn.edu.xmu.ooad.util.JacksonUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import jdk.dynalink.linker.LinkerServices;
//import netscape.javascript.JSObject;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RocketMQMessageListener(topic = "coupon-topic",consumerGroup = "coupon-group")
//public class CouponConsumerListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
//
//    @Autowired
//    private CouponDao couponDao;
//
//    private static final Logger logger = LoggerFactory.getLogger(CouponConsumerListener.class);
//
//
//    @Override
//    public void onMessage(String message) {
//        List<CouponPo> coupons = JSONObject.parseArray(JSON.toJSONString(message), CouponPo.class);
//        logger.debug("onMessage: got message coupons =" + coupons);
//        couponDao.insertCouponsBatch(coupons);
//    }
//
//    @Override
//    public void prepareStart(DefaultMQPushConsumer consumer) {
//        logger.info("prepareStart: consumergroup =" + consumer.getConsumerGroup());
//    }
//}
