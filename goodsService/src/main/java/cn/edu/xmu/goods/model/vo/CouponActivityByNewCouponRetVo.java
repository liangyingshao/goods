package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.CouponActivity;
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
        id= obj.getId();
        name=obj.getName();
        imageUrl=obj.getImageUrl();
        beginTime=obj.getBeginTime();
        endTime=obj.getEndTime();
        quantity=obj.getQuantity();
        couponTime=obj.getCouponTime();
    }
}
