package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.CouponSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="向活动添加SKU视图对象")
public class CouponSkuVo {
    private Long id;
    private Long activityId;
    public CouponSku createCouponSku()
    {
        CouponSku couponSku =new CouponSku();
        couponSku.setSkuId(id);
        couponSku.setActivityId(activityId);
        return couponSku;
    }
}
