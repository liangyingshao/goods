package cn.edu.xmu.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "查询SKU列表输入对象")
public class GoodsSkuVo {
    private Long shopId;
    private String skuSn;
    private Long spuId;
    private Long spuSn;
    private int page;
    private int pageSize;
}
