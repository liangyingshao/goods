package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.Coupon;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "获取优惠券信息视图对象")
public class CouponRetVo {

    private Long id;

    private CouponActivityByCouponRetVo activity;

    private String name;

    private String couponSn;

    public void set(Coupon obj)
    {
        id=obj.getId();
        name=obj.getName();
        couponSn=obj.getCouponSn();
    }
}
