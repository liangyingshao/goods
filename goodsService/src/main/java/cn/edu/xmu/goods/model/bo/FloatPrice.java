package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.FloatPricePo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class FloatPrice implements VoObject {

    public enum Validation {
        VALID(0, "可用"),
        INVALID(1, "废弃");

        private static final Map<Integer, FloatPrice.Validation> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (FloatPrice.Validation enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        Validation(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static FloatPrice.Validation getTypeByCode(Integer code) {
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

    private Long goodsSkuId;

    private Long activityPrice;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Integer quantity;

    private Long createdBy;

    private Long invalidBy;

    private Validation valid;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public FloatPrice()
    {

    }

    public FloatPrice(FloatPricePo po)
    {
        id=po.getId();
        goodsSkuId=po.getGoodsSkuId();
        activityPrice=po.getActivityPrice();
        beginTime=po.getBeginTime();
        endTime=po.getEndTime();
        quantity=po.getQuantity();
        createdBy=po.getCreatedBy();
        invalidBy=po.getInvalidBy();
        valid= Validation.getTypeByCode(po.getValid().intValue());
        gmtCreate=po.getGmtCreate();
        gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public FloatPricePo getFloatPricePo()
    {
        FloatPricePo floatPricePo=new FloatPricePo();
        floatPricePo.setId(id);
        floatPricePo.setGoodsSkuId(goodsSkuId);
        floatPricePo.setActivityPrice(activityPrice);
        floatPricePo.setBeginTime(beginTime);
        floatPricePo.setEndTime(endTime);
        floatPricePo.setQuantity(quantity);
        return floatPricePo;
    }
}
