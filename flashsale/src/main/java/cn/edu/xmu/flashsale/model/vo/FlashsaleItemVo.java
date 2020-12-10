package cn.edu.xmu.flashsale.model.vo;

import lombok.Data;

@Data
public class FlashsaleItemVo {
    private Long skuId;
    private Long price;
    private Integer quantity;

    public FlashsaleItemVo() {

    }
}
