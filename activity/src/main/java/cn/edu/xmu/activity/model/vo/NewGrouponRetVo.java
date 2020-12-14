package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.vo.GoodsSpuVo;
import cn.edu.xmu.goods.model.vo.SimpleSpuVo;
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
    SimpleSpuVo goodsSpu;
    SimpleShopDTO shop;
}
