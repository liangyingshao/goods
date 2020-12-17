package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.bo.Coupon;
import lombok.Data;

/**
 * description: ActivityStatusRetVo
 * date: 2020/12/16 22:12
 * author: 杨铭
 * version: 1.0
 */
@Data
public class ActivityStatusRetVo {
    private Long code;
    private String name;

    public ActivityStatusRetVo(ActivityStatus state) {
        code=state.getCode().longValue();
        name=state.getDescription();
    }
}
