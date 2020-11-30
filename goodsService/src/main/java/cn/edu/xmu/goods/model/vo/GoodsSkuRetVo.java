package cn.edu.xmu.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "查询SKU列表视图对象")
public class GoodsSkuRetVo {
    private Long id;

    //private Long goodsSpuId;

    private String skuSn;

    private String name;

    private Long originalPrice;

    //private String configuration;

    //private Long weight;

    private String imageUrl;

    private Integer inventory;

    //private String detail;

    private Byte disabled;

    //private Date gmtCreated;

    //private Date gmtModified;

}
