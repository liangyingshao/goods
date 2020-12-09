package cn.edu.xmu.flashsale.service;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import org.apache.dubbo.config.annotation.DubboReference;

public class FlashsaleService {
    @DubboReference
    private IGoodsService goodsService;

}
