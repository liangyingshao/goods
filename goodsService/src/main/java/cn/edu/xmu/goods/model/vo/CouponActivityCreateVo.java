package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.CouponActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

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
        couponActivity.setQuantitiyType(this.quantityType.byteValue());
        couponActivity.setValidTerm(this.validTerm.byteValue());
        couponActivity.setBeginTime(LocalDateTime.parse(this.beginTime));//或者直接设置为LocalDateTime类型?
        couponActivity.setEndTime(LocalDateTime.parse(this.endTime));
        return  couponActivity;
    }
}

