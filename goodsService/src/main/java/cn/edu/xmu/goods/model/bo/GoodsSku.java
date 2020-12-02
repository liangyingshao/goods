package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.BrandPo;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class GoodsSku implements VoObject {

    public enum State {
        ABLED(4, "可用"),
        DISABLED(5, "废弃");

        private static final Map<Integer, GoodsSku.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (GoodsSku.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static GoodsSku.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;

    private Long goodsSpuId;

    private String name;

    private String skuSn;

    private String imageUrl;

    private Integer inventory;

    private Long originalPrice;

    private Long price;

    private String configuration;

    private Long weight;

    private String detail;

    private State disabled;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    //private String spuSpec;

    public GoodsSku() {

    }

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
        disabled=State.getTypeByCode(po.getDisabled().intValue());
        gmtCreated=po.getGmtCreate();
        gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new GoodsSkuRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return new GoodsSkuRetVo(this);
    }

    public GoodsSkuPo getGoodsSkuPo() {
        GoodsSkuPo skuPo=new GoodsSkuPo();
        skuPo.setId(id);
        skuPo.setName(name);
        skuPo.setInventory(inventory);
        skuPo.setOriginalPrice(originalPrice);
        skuPo.setConfiguration(configuration);
        skuPo.setWeight(weight);
        skuPo.setDetail(detail);
        return skuPo;
    }

    public GoodsSkuPo getNewGoodsSkuPo()
    {
        GoodsSkuPo skuPo=new GoodsSkuPo();
        skuPo.setGoodsSpuId(goodsSpuId);
        skuPo.setSkuSn(skuSn);
        skuPo.setName(name);
        skuPo.setOriginalPrice(originalPrice);
        skuPo.setConfiguration(configuration);
        skuPo.setWeight(weight);
        skuPo.setImageUrl(imageUrl);
        skuPo.setInventory(inventory);
        skuPo.setDetail(detail);
        skuPo.setDisabled(disabled.getCode().byteValue());
        return skuPo;
    }
}
