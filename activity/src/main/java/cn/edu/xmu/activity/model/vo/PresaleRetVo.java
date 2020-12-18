package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.oomall.goods.model.SimpleGoodsSkuDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: PresaleRetVo
 * date: 2020/12/11 12:08
 * author: 杨铭
 * version: 1.0
 */
@Data
public class PresaleRetVo {
    Long id;
    String name;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    LocalDateTime payTime;
    SimpleGoodsSkuDTO goodsSku;
    SimpleShopDTO shop;

}
