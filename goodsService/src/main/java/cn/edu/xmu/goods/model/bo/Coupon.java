package cn.edu.xmu.goods.model.bo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Coupon {
    public enum State {
        UNAVAILABLE(0,"不可用"),
        AVAILABLE(1,"可用"),
        USED(2,"已使用"),
        DISABLED(3,"失效");

        private static final Map<Integer, Coupon.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Coupon.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Coupon.State getTypeByCode(Integer code) {
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

    private String couponSn;

    private String name;

    private Long customerId;

    private Long activityId;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Byte state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
