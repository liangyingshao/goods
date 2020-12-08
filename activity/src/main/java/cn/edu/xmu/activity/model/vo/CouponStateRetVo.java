package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.Coupon;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "查询优惠券状态种类视图对象")
public class CouponStateRetVo {
    private Long code;
    private String name;

    public CouponStateRetVo(Coupon.State state) {
        code=state.getCode().longValue();
        name=state.getDescription();
    }
}
