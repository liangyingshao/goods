package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.CouponSpu;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="向活动添加SPU视图对象")
public class CouponSpuVo {
    private Long id;
    public CouponSpu createCouponSpu()
    {
        CouponSpu couponSpu=new CouponSpu();
        couponSpu.setSpuId(id);
        return couponSpu;
    }
}
