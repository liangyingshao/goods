package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlashsaleItemRetVo implements VoObject {
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

    public FlashsaleItemRetVo(FlashSaleItem bo) {
        this.id = bo.getId();
        this.price = bo.getPrice();
        this.quantity = bo.getQuantity();
        this.gmtCreate = bo.getGmtCreate();
        this.gmtModified = bo.getGmtModified();
    }

    public FlashsaleItemRetVo() {

    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
