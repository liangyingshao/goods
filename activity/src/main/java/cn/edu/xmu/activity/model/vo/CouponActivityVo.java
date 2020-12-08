package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.CouponActivity;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 优惠活动 VO
 * @author 24320182203254 秦楚彦
 * @date 2020/12/04 15:54
 */
@Data
@ApiModel(description = "查看优惠活动信息视图对象")
public class CouponActivityVo {

    private Long id;

    private String name;

    private Byte state;

    private SimpleShopDTO shopVo;

    @Min(0)
    private Integer quantity;

    private Byte quantitiyType;

    private Byte validTerm;

    private String imageUrl;

    private String beginTime;

    private String endTime;

    private String couponTime;

    private String strategy;

    private CreatedBy createdBy;

    private ModifiedBy modifiedBy;

    private String gmtCreate;//到底写成string类型还是LocalDateTime类型？

    private String gmtModified;


    /**
     * 构造函数
     * @param bo Bo对象
     */

    public CouponActivityVo(CouponActivity bo){
        this.id=bo.getId();
        this.name=bo.getName();
        this.state=bo.getState().getCode().byteValue();
        this.quantity=bo.getQuantity();
        this.quantitiyType=bo.getQuantitiyType().getCode().byteValue();
        this.validTerm=bo.getValidTerm();
        this.imageUrl=bo.getImageUrl();
        this.beginTime=bo.getBeginTime().toString();
        this.endTime=bo.getEndTime().toString();
        this.couponTime=bo.getCouponTime().toString();
        this.strategy=bo.getStrategy();
        this.state=bo.getState().getCode().byteValue();
        this.gmtCreate=bo.getGmtCreate().toString();
        this.gmtModified=bo.getGmtModified().toString();
    }
}