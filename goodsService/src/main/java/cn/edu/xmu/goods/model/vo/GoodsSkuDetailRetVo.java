package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "查询SKU详细信息视图对象")
public class GoodsSkuDetailRetVo{
    private Long id;

    private String name;

    private String skuSn;

    private String detail;

    private String imageUrl;

    private Integer inventory;

    private Long originalPrice;

    private Long price;

    private String configuration;

    private Long weight;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private GoodsSpuVo spu;

    public void set(GoodsSku obj)
    {
        id=obj.getId();
        name=obj.getName();
        skuSn=obj.getSkuSn();
        detail = obj.getDetail();
        imageUrl=obj.getImageUrl();
        inventory=obj.getInventory();
        originalPrice=obj.getOriginalPrice();
        price=obj.getPrice();
        configuration = obj.getConfiguration();
        weight = obj.getWeight();
        gmtCreated = obj.getGmtCreated();
        gmtModified = obj.getGmtModified();
    }
}
