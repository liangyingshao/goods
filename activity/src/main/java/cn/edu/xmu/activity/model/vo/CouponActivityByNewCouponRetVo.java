package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.CouponActivity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "创建优惠券优惠活动部分视图对象")
public class CouponActivityByNewCouponRetVo {
    private Long id;

    private String name;

    private String imageUrl;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Integer quantity;

    private LocalDateTime couponTime;



    public void set(CouponActivity obj)
    {
        this.id= obj.getId();
        this.name=obj.getName();
        this.imageUrl=obj.getImageUrl();
        this.beginTime=obj.getBeginTime();
        this.endTime=obj.getEndTime();
        this.quantity=obj.getQuantity();
        this.couponTime=obj.getCouponTime();
    }
}
