package cn.edu.xmu.activity;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {
        "cn.edu.xmu.ooad",
        "cn.edu.xmu.activity"
})
@MapperScan("cn.edu.xmu.activity.mapper")
@EnableDubbo
@EnableDiscoveryClient
public class ActivityServiceApplication implements ApplicationRunner{
    private  static  final Logger logger = LoggerFactory.getLogger(ActivityServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ActivityServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Initialize......");
    }
}
