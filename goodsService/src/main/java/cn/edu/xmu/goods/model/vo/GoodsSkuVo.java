package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "可修改的SKU信息")
public class GoodsSkuVo {
    @NotBlank
    private String name;
    @Min(0)
    private Integer inventory;
    @Min(0)
    private Long originalPrice;

    private String configuration;
    @Min(0)
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
