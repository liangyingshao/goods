package cn.edu.xmu.goods.service.mq;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "sku-topic",consumerGroup = "sku-group")
public class SkuConsumerListener  implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private GoodsDao goodsDao;

    private static final Logger logger = LoggerFactory.getLogger(SkuConsumerListener.class);

    @Override
    public void onMessage(String message) {
        GoodsSkuPo skuPo= JacksonUtil.toObj(message, GoodsSkuPo.class);
        logger.debug("onMessage: got message skuPo =" + skuPo);
        goodsDao.updateByPrimaryKeySelective(skuPo);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        logger.info("prepareStart: consumergroup =" + consumer.getConsumerGroup());
    }
}
