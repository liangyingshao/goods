package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class GoodsSku implements VoObject {

    public enum State {
        OFFSHELF(0,"未上架"),
        ONSHELF(4,"上架"),
        DELETED(6,"已删除");

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

    public enum Disable {
        OPEN(0,"开放"),
        CLOSE(1,"关闭");

        private static final Map<Integer, GoodsSku.Disable> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (GoodsSku.Disable enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        Disable(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static GoodsSku.Disable getTypeByCode(Integer code) {
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

    private Disable disabled;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private State state;

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
        disabled=Disable.getTypeByCode(po.getDisabled().intValue());
        gmtCreated=po.getGmtCreate();
        gmtModified=po.getGmtModified();
        state=State.getTypeByCode(po.getState().intValue());
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
