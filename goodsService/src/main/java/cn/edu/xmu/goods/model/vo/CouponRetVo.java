package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Coupon;
import cn.edu.xmu.goods.model.po.CouponPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

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
