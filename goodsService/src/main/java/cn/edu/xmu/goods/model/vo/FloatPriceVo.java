package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.FloatPrice;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "可修改的信息")
public class FloatPriceVo {
    @Min(0)
    private Long activityPrice;
    @Future
    private LocalDateTime beginTime;
    @Future
    private LocalDateTime endTime;
    @Min(0)
    private Integer quantity;

    public FloatPrice createFloatPrice()
    {
        FloatPrice floatPrice=new FloatPrice();
        floatPrice.setActivityPrice(activityPrice);
        floatPrice.setBeginTime(beginTime);
        floatPrice.setEndTime(endTime);
        floatPrice.setQuantity(quantity);
        return floatPrice;
    }
}
