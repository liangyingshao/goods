package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "可修改的SKU信息")
public class GoodsSkuVo {
    private String name;

    private Integer inventory;

    private Long originalPrice;

    private String configuration;

    private Long weight;

    private String detail;

    public GoodsSku createGoodsSku() {
        GoodsSku sku=new GoodsSku();
        sku.setName(name);
        sku.setInventory(inventory);
        sku.setOriginalPrice(originalPrice);
        sku.setConfiguration(configuration);
        sku.setWeight(weight);
        sku.setDetail(detail);
        return sku;
    }
}
