package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlashsaleItemRetVo {
    private Long id;
    private SkuInfoDTO goodsSku;
    private Long price;
    private Integer quantity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public FlashsaleItemRetVo(FlashSaleItemPo po) {
        this.id = po.getId();
        this.price = po.getPrice();
        this.quantity = po.getQuantity();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }
}
