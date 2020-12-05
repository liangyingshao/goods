package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.CouponActivityPo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class CouponActivity {
    public enum State {
        TO_BE_ONLINE(0, "待上线"),
        ONLINE(1, "进行中"),
        OFFLINE(3,"已下线"),
        FINISHED(4,"活动结束");

        private static final Map<Integer, CouponActivity.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (CouponActivity.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static CouponActivity.State getTypeByCode(Integer code) {
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

    private String name;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private LocalDateTime couponTime;

    private State state;

    private Long shopId;

    private Integer quantity;

    private Byte validTerm;

    private String imageUrl;

    private String strategy;

    private Long createdBy;

    private Long modiBy;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Byte quantitiyType;

    public CouponActivity(){

    }

    /**
     * 构造函数 po->bo
     * @param po Po对象
     */
    public CouponActivity(CouponActivityPo po){
        this.id=po.getId();
        this.name=po.getName();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.couponTime=po.getCouponTime();
        this.state=State.getTypeByCode(po.getState().intValue());
        this.shopId=po.getShopId();
        this.quantity=po.getQuantity();
        this.validTerm=po.getValidTerm();
        this.imageUrl=po.getImageUrl();
        this.strategy=po.getStrategy();
        this.createdBy=po.getCreatedBy();
        this.modiBy=po.getModiBy();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.quantitiyType=po.getQuantitiyType();

    }

    /**
     * 构造函数 bo->po
     * @return couponActivityPo对象
     * Created at 2020/12/04 23：01
     */
    public CouponActivityPo createActivityPo(){
        CouponActivityPo activityPo=new CouponActivityPo();
        activityPo.setId(this.id);
        activityPo.setName(this.name);
        activityPo.setBeginTime(this.beginTime);
        activityPo.setEndTime(this.endTime);
        activityPo.setCouponTime(this.couponTime);
        activityPo.setState(this.state.getCode().byteValue());
        activityPo.setShopId(this.shopId);
        activityPo.setQuantity(this.quantity);
        activityPo.setQuantitiyType(this.quantitiyType);
        activityPo.setValidTerm(this.getValidTerm());
        activityPo.setImageUrl(this.imageUrl);
        activityPo.setStrategy(this.strategy);
        activityPo.setCreatedBy(this.createdBy);
        activityPo.setModiBy(this.modiBy);
        activityPo.setGmtCreate(this.gmtCreate);
        activityPo.setGmtModified(this.gmtModified);
        return activityPo;
    }
}
