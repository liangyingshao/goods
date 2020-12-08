package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.CouponSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "添加SKU的视图对象")
public class CouponSkuRetVo {
    private Long skuId;

    public void set(CouponSku obj)
    {
        skuId=obj.getSkuId();
    }
}
