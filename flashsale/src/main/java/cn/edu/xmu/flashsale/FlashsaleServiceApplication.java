package cn.edu.xmu.flashsale;

import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = {
        "cn.edu.xmu.ooad",
        "cn.edu.xmu.flashsale"
})
@MapperScan("cn.edu.xmu.flashsale.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.goods.flashsale.impl")
@EnableDiscoveryClient
public class FlashsaleServiceApplication implements ApplicationRunner{
    private  static  final Logger logger = LoggerFactory.getLogger(FlashsaleServiceApplication.class);

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private FlashSaleDao flashSaleDao;

    @Autowired
    private FlashSaleItemDao flashSaleItemDao;

    public static void main(String[] args) {
        SpringApplication.run(FlashsaleServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Initialize......");
        loadRedis();
    }


    public void loadRedis() {
        //准备好秒杀id
        List<FlashSalePo> flashSalePosToday = flashSaleDao.selectByFlashDate(LocalDateTime.now()).getData();
        List<FlashSalePo> flashSalePosTom = flashSaleDao.selectByFlashDate(LocalDateTime.now().plusDays(1)).getData();
        //将今天的item按照flashsale-segment存进redis
        for (FlashSalePo po:flashSalePosToday) {
            List<FlashSaleItemPo> itemPos = flashSaleItemDao.selectByFlashsaleId(po.getId()).getData();
            String key = "FlashSaleItem:" + po.getFlashDate().toLocalDate().toString() + po.getTimeSegId().toString();
            for (FlashSaleItemPo itemPo : itemPos) {
                redisTemplate.boundSetOps(key).add((Serializable) itemPo);
            }
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
        logger.error("flashSalePosToday:"+String.valueOf(flashSalePosToday.size()));
        //将明天的item存进redis
        for (FlashSalePo po:flashSalePosTom) {
            List<FlashSaleItemPo> itemPos = flashSaleItemDao.selectByFlashsaleId(po.getId()).getData();
            String key = "FlashSaleItem:" + po.getFlashDate().toLocalDate().toString() + po.getTimeSegId().toString();
            for (FlashSaleItemPo itemPo : itemPos) {
                redisTemplate.boundSetOps(key).add((Serializable) itemPo);
            }
            redisTemplate.expire(key, 48, TimeUnit.HOURS);
        }
        logger.error("flashSalePosTom:"+flashSalePosTom.size());
    }
}
