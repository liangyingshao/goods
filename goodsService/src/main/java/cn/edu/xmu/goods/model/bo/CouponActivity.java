package cn.edu.xmu.goods.model.bo;

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
}
