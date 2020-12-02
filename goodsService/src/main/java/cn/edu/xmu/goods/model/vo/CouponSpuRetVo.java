package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.CouponSpu;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "添加SPU的视图对象")
public class CouponSpuRetVo {
    private Long id;

    private Long activityId;

    private GoodsSpuCouponCreateRetVo spu;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public void set(CouponSpu obj)
    {
        id=obj.getId();
        activityId=obj.getActivityId();
        gmtCreate=obj.getGmtCreate();
        gmtModified=obj.getGmtModified();
    }
}
