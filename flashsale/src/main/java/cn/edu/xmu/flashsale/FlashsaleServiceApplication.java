package cn.edu.xmu.flashsale;

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

import java.io.Serializable;

@SpringBootApplication(scanBasePackages = {
        "cn.edu.xmu.ooad",
        "cn.edu.xmu.flashsale"
})
@MapperScan("cn.edu.xmu.flashsale.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.goods.falshsale.impl")
@EnableDiscoveryClient
public class FlashsaleServiceApplication implements ApplicationRunner{
    private  static  final Logger logger = LoggerFactory.getLogger(FlashsaleServiceApplication.class);

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(FlashsaleServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Initialize......");
    }
}
