package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSpu;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "向活动添加SPU的SPU部分视图对象")
public class GoodsSpuCouponCreateRetVo {
    private Long id;

    private String name;

    private Long brandId;

    private String goodsSn;

    private String imageUrl;

    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

//    private Boolean disabled;

    public void set(GoodsSpu obj)
    {
        id=obj.getId();
        name=obj.getName();
        brandId= obj.getBrandId();
        goodsSn= obj.getGoodsSn();
        imageUrl= obj.getImageUrl();
        state=obj.getState().getCode().intValue();
        gmtCreate=obj.getGmtCreated();
        gmtModified=obj.getGmtModified();
//        disabled=obj.isDisabled();
    }
}
