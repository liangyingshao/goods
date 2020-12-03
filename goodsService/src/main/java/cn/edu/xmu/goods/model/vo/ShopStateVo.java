package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.ShopPo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员状态VO
 * @author LiangJi3229
 * @date 2020/11/10 18:41
 */
@Data
public class ShopStateVo {

    private Long Code;
    private String name;

    public enum ShopStatus {
        //商-店铺：0：未审核，1：未上线，2：上线，3：关闭，4：审核未通过
        NOT_AUDIT(0, "未审核"),
        OFFLINE(1, "未上线"),
        ONLINE(2, "上线"),
        CLOSED(3, "关闭"),
        AUDIT_FAIL(4, "审核未通过");

        private static final Map<Integer, ShopStateVo.ShopStatus> typeMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            typeMap = new HashMap();
            for (ShopStateVo.ShopStatus enum1 : values()) {
                typeMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;


        ShopStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }
        public Integer getCode() {
            return code;
        }
        public String getDescription() {
            return description;
        }
    }


    public ShopStateVo(ShopStatus state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
