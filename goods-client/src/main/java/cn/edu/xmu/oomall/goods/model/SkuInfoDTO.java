package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuInfoDTO implements Serializable {
    private Long skuId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Integer inventory;
    private Long originalPrice;
    private Long price;
    private Boolean disable;
}
