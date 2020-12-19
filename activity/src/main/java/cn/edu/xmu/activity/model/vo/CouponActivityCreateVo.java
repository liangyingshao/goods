package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.CouponActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 新增优惠活动传入VO
 * @author 24320182203254 秦楚彦
 * @date 2020/12/04 21:02
 */
@Data
@ApiModel(description = "新增优惠活动信息视图对象")
public class CouponActivityCreateVo {
    @ApiModelProperty(name = "优惠活动名")
    private String name;

    @Min(0)
    @ApiModelProperty(name = "优惠券数目")
    private Integer quantity;

    @ApiModelProperty(name = "优惠券类型")
    private Integer quantityType;

    @ApiModelProperty(name = "自领取之时有效时间")
    private Integer validTerm;


    @ApiModelProperty(name = "活动开始时间")
    private String beginTime;


    @ApiModelProperty(name = "活动结束时间")
    private String endTime;

    @ApiModelProperty(name = "活动策略")
    private String strategy;

    /**
     * 根据Vo构造Po函数
     */
    public CouponActivity createActivity() {
        CouponActivity couponActivity = new CouponActivity();
        couponActivity.setName(this.name);
        couponActivity.setQuantity(this.quantity);
        couponActivity.setStrategy(this.strategy);
        couponActivity.setQuantitiyType(CouponActivity.Type.getTypeByCode(this.quantityType));
        if(this.validTerm!=null)
        couponActivity.setValidTerm(this.validTerm.byteValue());
        if(this.beginTime!=null)
        couponActivity.setBeginTime(LocalDateTime.parse(this.beginTime,DateTimeFormatter.ISO_DATE_TIME));
        if(this.endTime!=null)
        couponActivity.setEndTime(LocalDateTime.parse(this.endTime,DateTimeFormatter.ISO_DATE_TIME));
        return  couponActivity;
    }
}

