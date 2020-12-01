package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsSkuPo;
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
        ABLED(0, "可用"),
        DISABLED(1, "废弃");

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

    //private Long price;

    private String configuration;

    private Long weight;

    private String detail;

    private State disabled;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

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
        skuRetVo.setDisabled(disabled.getCode().byteValue());
        skuRetVo.setGmtCreated(gmtCreated);
        skuRetVo.setGmtModified(gmtModified);
        return skuRetVo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
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
}
