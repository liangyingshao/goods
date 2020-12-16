package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.oomall.goods.model.SimpleGoodsSkuDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import lombok.Data;

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
    String beginTime;
    String endTime;
    String payTime;
    SimpleGoodsSkuDTO goodsSku;
    SimpleShopDTO shop;

}
