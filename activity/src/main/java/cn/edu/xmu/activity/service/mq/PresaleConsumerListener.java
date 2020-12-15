package cn.edu.xmu.activity.service.mq;

import cn.edu.xmu.activity.dao.PresaleDao;
import cn.edu.xmu.activity.model.po.CouponPo;
import cn.edu.xmu.activity.model.po.PresaleActivityPo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RocketMQMessageListener(topic = "presale-topic",consumerGroup = "presale-group")
public class PresaleConsumerListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private PresaleDao presaleDao;

    private static final Logger logger = LoggerFactory.getLogger(PresaleConsumerListener.class);

    @Override
    public void onMessage(String message) {
        PresaleActivityPo presaleActivityPo= JacksonUtil.toObj(message, PresaleActivityPo.class);
        logger.debug("onMessage: got message presaleActivityPo =" + presaleActivityPo);
        presaleDao.updateQuantityByPrimaryKeySelective(presaleActivityPo);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        logger.info("prepareStart: consumergroup =" + consumer.getConsumerGroup());
    }
}
