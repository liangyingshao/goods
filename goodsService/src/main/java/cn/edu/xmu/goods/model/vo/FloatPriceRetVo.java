package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "添加SKU的浮动价格库存项视图对象")
public class FloatPriceRetVo {

    private Long id;

    private Long activityPrice;

    private Integer quantity;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private CreatedBy createdBy;

    private ModifiedBy modifiedBy;

    public void set(FloatPrice obj) {
        id = obj.getId();
        activityPrice=obj.getActivityPrice();
        quantity=obj.getQuantity();
        beginTime=obj.getBeginTime();
        endTime=obj.getEndTime();
    }
}

