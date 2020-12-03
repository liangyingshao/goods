package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "在SPU下创建SKU的视图对象")
public class GoodsSkuBySpuVo {

    //private String spuSpec;

    private String sn;

    @NotBlank
    private String name;
    @Min(0)
    private Long originalPrice;

    private String configuration;

    @Min(0)
    private Long weight;

    private String imageUrl;

    @Min(0)
    private Integer inventory;

    private String detail;

    public GoodsSku createGoodsSku()
    {
        GoodsSku sku=new GoodsSku();
        //sku.setSpuSpec(spuSpec);
        sku.setSkuSn(sn);
        sku.setName(name);
        sku.setOriginalPrice(originalPrice);
        sku.setConfiguration(configuration);
        sku.setWeight(weight);
        sku.setImageUrl(imageUrl);
        sku.setInventory(inventory);
        sku.setDetail(detail);
        return sku;
    }
}
