package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel(description = "查询SKU列表视图对象")
public class GoodsSkuRetVo {
    private Long id;

    private String name;

    private String skuSn;

    private String imageUrl;

    private Integer inventory;

    private Long originalPrice;

    private Long price;

    private Boolean disable;

    public GoodsSkuRetVo(GoodsSku obj)
    {
        id=obj.getId();
        name=obj.getName();
        skuSn=obj.getSkuSn();
        imageUrl=obj.getImageUrl();
        inventory=obj.getInventory();
        originalPrice=obj.getOriginalPrice();
        disable= !GoodsSku.Disable.getTypeByCode(obj.getDisabled().getCode()).equals(GoodsSku.Disable.OPEN);
        price=obj.getPrice();
    }
}
