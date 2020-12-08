package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.Coupon;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "领取优惠券视图对象")
public class CouponNewRetVo {
    private Long id;

//    private CouponActivityByNewCouponRetVo activity;

    private Long customerId;

    private String name;

    private String couponSn;

    private Integer state;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public void set(Coupon obj)
    {
        id=obj.getId();
        customerId=obj.getCustomerId();
        name=obj.getName();
        couponSn=obj.getCouponSn();
        state=obj.getState().getCode().intValue();
        beginTime=obj.getBeginTime();
        endTime=obj.getEndTime();
        gmtCreate=obj.getGmtCreate();
        gmtModified=obj.getGmtModified();
    }
}
