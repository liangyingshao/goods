package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class GoodsSku implements VoObject {

    private Long id;

    private Long goodsSpuId;

    private String name;

    private String skuSn;

    private String imageUrl;

    private Integer inventory;

    private Long originalPrice;

    //private Long price;

    private String configuration;

    private Long weight;

    private String detail;

    private Byte disabled;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public GoodsSku(GoodsSkuPo po) {
        id=po.getId();
        goodsSpuId=po.getGoodsSpuId();
        skuSn=po.getSkuSn();
        name=po.getName();
        originalPrice=po.getOriginalPrice();
        configuration=po.getConfiguration();
        weight=po.getWeight();
        imageUrl=po.getImageUrl();
        inventory=po.getInventory();
        detail=po.getDetail();
        disabled=po.getDisabled();
        gmtCreated=po.getGmtCreated();
        gmtModified=po.getGmtModified();
    }



    @Override
    public Object createVo() {
        GoodsSkuRetVo skuRetVo=new GoodsSkuRetVo();
        skuRetVo.setId(id);
        //skuRetVo.setGoodsSpuId(goodsSpuId);
        skuRetVo.setSkuSn(skuSn);
        skuRetVo.setName(name);
        skuRetVo.setOriginalPrice(originalPrice);
        //skuRetVo.setConfiguration(configuration);
        //skuRetVo.setWeight(weight);
        skuRetVo.setImageUrl(imageUrl);
        skuRetVo.setInventory(inventory);
        //skuRetVo.setDetail(detail);
        skuRetVo.setDisabled(disabled);
        skuRetVo.setGmtCreated(gmtCreated);
        skuRetVo.setGmtModified(gmtModified);
        return skuRetVo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
