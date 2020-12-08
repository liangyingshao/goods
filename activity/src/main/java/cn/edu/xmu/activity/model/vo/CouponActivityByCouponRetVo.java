package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.CouponActivity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "返回优惠券信息里的优惠活动部分视图对象")
public class CouponActivityByCouponRetVo {
    private Long id;

    private String name;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Integer quantity;

    private LocalDateTime couponTime;

    public void set(CouponActivity obj)
    {
        id= obj.getId();
        name=obj.getName();
        beginTime=obj.getBeginTime();
        endTime=obj.getEndTime();
        quantity=obj.getQuantity();
        couponTime=obj.getCouponTime();
    }
}
