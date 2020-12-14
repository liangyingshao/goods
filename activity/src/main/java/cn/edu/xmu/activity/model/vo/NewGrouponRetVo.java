package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.oomall.goods.model.GoodsSpuPoDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import lombok.Data;

/**
 * description: NewGrouponRetVo
 * date: 2020/12/10 15:45
 * author: 杨铭
 * version: 1.0
 */
@Data
public class NewGrouponRetVo {
    Long id;
    String name;
    GoodsSpuPoDTO goodsSpuPoDTO;
    SimpleShopDTO simpleShopDTO;
}
