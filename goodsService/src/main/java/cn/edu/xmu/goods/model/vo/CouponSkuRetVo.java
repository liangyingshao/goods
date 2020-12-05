package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.CouponSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "添加SKU的视图对象")
public class CouponSkuRetVo {
    private Long skuId;

    public void set(CouponSku obj)
    {
        skuId=obj.getSkuId();
    }
}
