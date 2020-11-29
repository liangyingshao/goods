package cn.edu.xmu.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author Chuyan Qin
 **/
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.goods"})
@MapperScan("cn.edu.xmu.goods.mapper")

public class GoodsServiceApplication implements ApplicationRunner{
    private  static  final Logger logger = LoggerFactory.getLogger(GoodsServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GoodsServiceApplication.class, args);


    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
            logger.debug("Initialize......");

        }
    }
