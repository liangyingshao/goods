package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: SimpleGoodsSkuDTO
 * date: 2020/12/11 11:53
 * author: 杨铭
 * version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleGoodsSkuDTO {
    Long id;
    String name;
    String skuSn;
    String imageUrl;
    Integer inventory;
    Long originalPrice;
    //Long Price;
    Byte disabled;
}
