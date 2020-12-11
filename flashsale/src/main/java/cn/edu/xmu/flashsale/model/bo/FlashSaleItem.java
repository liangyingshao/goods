package cn.edu.xmu.flashsale.model.bo;

import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.vo.FlashsaleItemRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlashSaleItem implements VoObject {
    public FlashSaleItem() {

    }

    public FlashSaleItem(FlashSaleItemPo po) {
        this.id = po.getId();
        this.skuId = po.getGoodsSkuId();
        this.price = po.getPrice();
        this.quantity = po.getQuantity();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    private Long id;
    private Long skuId;
    private Long price;
    private Integer quantity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
