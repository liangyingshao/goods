package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.FloatPrice;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "可修改的信息")
public class FloatPriceVo {
    @Min(0)
    private Long activityPrice;

    @DateTimeFormat
    @Future
    private LocalDateTime beginTime;

    @DateTimeFormat
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
