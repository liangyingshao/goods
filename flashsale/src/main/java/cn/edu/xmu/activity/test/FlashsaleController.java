package cn.edu.xmu.activity.test;

import cn.edu.xmu.oomall.goods.service.IGoodsService;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "秒杀服务", tags = "flashsale")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
public class FlashsaleController {
    private  static  final Logger logger = LoggerFactory.getLogger(FlashsaleController.class);

    @DubboReference
    private IGoodsService IGoodsService;

    public void Test()
    {
        Long testILong = 0L;
        testILong = IGoodsService.getShopIdBySpuId(100L);
        logger.debug("成功调用goodsService的微服务：", testILong);
    }
}
