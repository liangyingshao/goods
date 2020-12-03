package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSpu;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "查询优惠活动的SPU列表时视图对象")
public class GoodsSpuCouponRetVo {
    private Long id;
    private String name;

    public void set(GoodsSpu obj) {
        id = obj.getId();
        name=obj.getName();
    }
}
