package cn.edu.xmu.activity.model.bo;

import cn.edu.xmu.activity.model.po.CouponActivityPo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class CouponActivity {

    public enum State {
        TO_BE_ONLINE(0, "待上线"),
        ONLINE(1, "明天开始"),
        OFFLINE(2,"进行中"),
        FINISHED(3,"活动结束");

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
    public enum DatabaseState {
        OFFLINE(0, "已下线"),
        ONLINE(1, "已上线"),
        DELETED(2,"已删除");

        private static final Map<Integer, CouponActivity.DatabaseState> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (CouponActivity.DatabaseState enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        DatabaseState(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static CouponActivity.DatabaseState getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Type {
        LIMIT_PER_PERSON(0, "每人数量"),
        LIMIT_TOTAL_NUM(1, "总量控制");

        private static final Map<Integer, CouponActivity.Type> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (CouponActivity.Type enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }
        private int code;
        private String description;

        Type(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static CouponActivity.Type getTypeByCode(Integer code) {
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

    private DatabaseState state;

    private Long shopId;

    private Integer quantity;

    private Byte validTerm;

    private String imageUrl;

    private String strategy;

    private Long createdBy;

    private Long modiBy;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Type quantitiyType;

    public CouponActivity(){

    }

    /**
     * 构造函数 po->bo
     * @param po Po对象
     */
    public CouponActivity(CouponActivityPo po){
        id=po.getId();
        name=po.getName();
        beginTime=po.getBeginTime();
        endTime=po.getEndTime();
        couponTime=po.getCouponTime();
        state=DatabaseState.getTypeByCode(po.getState().intValue());
        shopId=po.getShopId();
        quantity=po.getQuantity();
        validTerm=po.getValidTerm();
        imageUrl=po.getImageUrl();
        strategy=po.getStrategy();
        createdBy=po.getCreatedBy();
        modiBy=po.getModiBy();
        gmtCreate=po.getGmtCreate();
        gmtModified=po.getGmtModified();
        if(po.getQuantitiyType()!=null)
        quantitiyType=CouponActivity.Type.getTypeByCode(po.getQuantitiyType().intValue());

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
        activityPo.setQuantitiyType(this.quantitiyType.getCode().byteValue());
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
